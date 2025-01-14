package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.service.CouponDiscountService;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.service.PackageService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderBookServiceImpl implements OrderBookService {

    private final OrderBookRepository orderBookRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final CouponDiscountService couponDiscountService;
    private final PackageService packageService;

    @Override
    @Transactional
    public OrderBookResponseDto createOrderBook(OrderBookRequestDto orderBookRequestDto) {
        OrderBook savedOrderBook = processSingleOrderBook(orderBookRequestDto);
        return toOrderBookResponseDto(findEntityById(savedOrderBook.getOrderBookId(), orderBookRepository, "OrderBook"));
    }

    @Override
    @Transactional
    public List<OrderBookResponseDto> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos) {
        return orderBookRequestDtos.stream()
                .map(this::processSingleOrderBook)
                .map(orderBook -> toOrderBookResponseDto(findEntityById(orderBook.getOrderBookId(), orderBookRepository, "OrderBook")))
                .collect(Collectors.toList());
    }

    @Override
    public OrderBookResponseDto getOrderBook(Long orderBookId) {
        return toOrderBookResponseDto(findEntityById(orderBookId, orderBookRepository, "OrderBook"));
    }

    @Override
    @Transactional
    public OrderBookResponseDto updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState) {
        OrderBook orderBook = findEntityById(orderBookId, orderBookRepository, "OrderBook");
        orderBook.updateOrderBookState(newOrderBookState);
        return toOrderBookResponseDto(orderBookRepository.save(orderBook));
    }

    @Override
    @Transactional
    public void deleteOrderBook(Long orderBookId) {
        OrderBook orderBook = findEntityById(orderBookId, orderBookRepository, "OrderBook");
        orderBookRepository.delete(orderBook);
    }

    @Override
    public List<PackageResponseDto> getPackages(Long orderBookId) {
        OrderBook orderBook = findEntityById(orderBookId, orderBookRepository, "OrderBook");
        return orderBook.getPackages().stream()
                .map(this::toPackageResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponDiscountResponseDto getCouponDiscount(Long orderBookId) {
        OrderBook orderBook = findEntityById(orderBookId, orderBookRepository, "OrderBook");
        return toCouponDiscountResponseDto(orderBook.getCouponDiscount());
    }

    @Override
    public String getOrderName(List<OrderBookRequestDto> dtos) {
        if (dtos.size() == 1) {
            Book book = findEntityById(dtos.getFirst().getBookId(), bookRepository, "Book");
            return book.getTitle() + " " + dtos.getFirst().getQuantity() + "권";
        }
        Book book = findEntityById(dtos.getFirst().getBookId(), bookRepository, "Book");
        return book.getTitle() + " 외 " + (dtos.size() - 1) + "권";
    }

    private OrderBook processSingleOrderBook(OrderBookRequestDto dto) {
        Book book = findEntityById(dto.getBookId(), bookRepository, "Book");

        Order order = findEntityById(dto.getOrderId(), orderRepository, "Order");
        OrderBook savedOrderBook = orderBookRepository.save(dto.toEntity(book, order));

        if (dto.getCouponDiscountRequestDto() != null) {
            couponDiscountService.createCouponDiscount(dto.getCouponDiscountRequestDto(), savedOrderBook);
            //couponService.useCoupon(order.getUser().getUserId(), dto.getCouponId());
        }

        if (dto.getPackagesRequestDtos() != null && !dto.getPackagesRequestDtos().isEmpty()) {
            dto.getPackagesRequestDtos().forEach(pkgDto -> packageService.createPackage(pkgDto, savedOrderBook));
        }

        return savedOrderBook;
    }

    private PackageResponseDto toPackageResponseDto(Packages pkg) {
        return PackageResponseDto.builder()
                .packageId(pkg.getPackageId())
                .packageType(pkg.getPackageType())
                .build();
    }

    private CouponDiscountResponseDto toCouponDiscountResponseDto(CouponDiscount couponDiscount) {
        if (couponDiscount == null) return null;
        return CouponDiscountResponseDto.builder()
                .couponDiscountId(couponDiscount.getCouponDiscountId())
                .couponName(couponDiscount.getCouponName())
                .couponType(couponDiscount.getCouponType())
                .discountPrice(couponDiscount.getDiscountPrice())
                .build();
    }

    @Override
    public OrderBookResponseDto toOrderBookResponseDto(OrderBook orderBook) {
        CouponDiscountResponseDto couponRes = toCouponDiscountResponseDto(orderBook.getCouponDiscount());
        List<PackageResponseDto> packageResList = orderBook.getPackages().stream()
                .map(this::toPackageResponseDto)
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

    @Override
    public List<OrderBook> getOrderBooks(Long orderId) {
        return orderBookRepository.findByOrderOrderId(orderId);
    }

    private <T> T findEntityById(Long id, JpaRepository<T, Long> repository, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName + " not found. ID=" + id));
    }
}


