package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.service.CouponDiscountService;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.exception.OrderNotFoundException;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.service.PackageService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderBookServiceImpl implements OrderBookService {

    private final OrderBookRepository orderBookRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final BookManagementService bookManagementService;
    private final CouponDiscountService couponDiscountService;
    private final PackageService packageService;

    @Override
    @Transactional
    public List<OrderBookResponseDto> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos) {
        List<OrderBookResponseDto> resultList = new ArrayList<>();

        for (OrderBookRequestDto dto : orderBookRequestDtos) {
            // 1. Book 찾기 & 재고 차감
            Book book = bookRepository.findById(dto.getBookId())
                    .orElseThrow(() -> new NotFoundException("Book not found. ID=" + dto.getBookId()));
            bookManagementService.modifyQuantity(book.getBookId(), -dto.getQuantity());

            // 2. Order(주문) 찾기
            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException("Order not found. ID=" + dto.getOrderId()));

            // 3. OrderBook 엔티티 생성 & DB 저장
            OrderBook savedOrderBook = orderBookRepository.save(dto.toEntity(book, order));

            // 4. 쿠폰 할인 생성 (CouponDiscountService 사용)
            if (dto.getCouponDiscountRequestDto() != null) {
                couponDiscountService.createCouponDiscount(dto.getCouponDiscountRequestDto(), savedOrderBook);
                // 내부에서 savedOrderBook.setCouponDiscount
            }

            // 5. 패키지 생성 (PackageService 사용) - 여러 개
            if (dto.getPackagesRequestDtos() != null && !dto.getPackagesRequestDtos().isEmpty()) {
                for (PackageRequestDto pkgDto : dto.getPackagesRequestDtos()) {
                    packageService.createPackage(pkgDto, savedOrderBook);
                    // 내부에서 savedOrderBook.addPackage
                }
            }

            // 6. DB에서 다시 OrderBook을 조회하여 최종 상태 확인 (optional)
            OrderBook finalOrderBook = orderBookRepository.findById(savedOrderBook.getOrderBookId())
                    .orElseThrow(() -> new NotFoundException("OrderBook not found after creation"));

            resultList.add(toOrderBookResponseDto(finalOrderBook));
        }

        return resultList;
    }


    @Override
    public OrderBookResponseDto getOrderBook(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new IllegalArgumentException("OrderBook not found for ID: " + orderBookId));

        return toOrderBookResponseDto(orderBook);
    }

    @Override
    @Transactional
    public OrderBookResponseDto updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new IllegalArgumentException("OrderBook not found for ID: " + orderBookId));

        orderBook.updateOrderBookState(newOrderBookState);

        OrderBook updatedOrderBook = orderBookRepository.save(orderBook);
        return toOrderBookResponseDto(updatedOrderBook);
    }

    @Override
    @Transactional
    public void deleteOrderBook(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found for ID: " + orderBookId));
        orderBookRepository.delete(orderBook);
    }

    @Override
    public List<PackageResponseDto> getPackages(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found"));

        return orderBook.getPackages().stream()
                .map(this::toPackageResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponDiscountResponseDto getCouponDiscount(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found for ID: " + orderBookId));

        if (orderBook.getCouponDiscount() == null) {
            return null;
        }

        return toCouponDiscountResponseDto(orderBook.getCouponDiscount());
    }

    private PackageResponseDto toPackageResponseDto(Packages pkg) {
        return PackageResponseDto.builder()
                .packageId(pkg.getPackageId())
                .packageType(pkg.getPackageType())
                .build();
    }

    private CouponDiscountResponseDto toCouponDiscountResponseDto(CouponDiscount couponDiscount) {
        return CouponDiscountResponseDto.builder()
                .couponDiscountId(couponDiscount.getCouponDiscountId())
                .couponName(couponDiscount.getCouponName())
                .couponType(couponDiscount.getCouponType())
                .discountPrice(couponDiscount.getDiscountPrice())
                .build();
    }

    @Override
    public OrderBookResponseDto toOrderBookResponseDto(OrderBook orderBook) {
        // 쿠폰
        CouponDiscountResponseDto couponRes = null;
        if (orderBook.getCouponDiscount() != null) {
            couponRes = CouponDiscountResponseDto.builder()
                    .couponDiscountId(orderBook.getCouponDiscount().getCouponDiscountId())
                    .couponName(orderBook.getCouponDiscount().getCouponName())
                    .couponType(orderBook.getCouponDiscount().getCouponType())
                    .discountPrice(orderBook.getCouponDiscount().getDiscountPrice())
                    .build();
        }

        // 패키지
        List<PackageResponseDto> packageResList = orderBook.getPackages().stream()
                .map(pkg -> PackageResponseDto.builder()
                        .packageId(pkg.getPackageId())
                        .packageType(pkg.getPackageType())
                        .build())
                .collect(Collectors.toList());

        return OrderBookResponseDto.builder()
                .orderBookId(orderBook.getOrderBookId())
                .bookTitle(orderBook.getBook().getTitle())
                .quantity(orderBook.getQuantity())
                .salePrice(orderBook.getSalePrice())
                .discountPrice(orderBook.getDiscountPrice())
                .orderBookState(orderBook.getOrderBookState().name())
                .couponDiscount(couponRes)
                .packages(packageResList)
                .build();
    }
}


