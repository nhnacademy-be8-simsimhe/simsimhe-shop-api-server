package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.exception.BookOutOfStockException;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.exception.OrderNotFoundException;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderBookServiceImpl implements OrderBookService {

    private final OrderBookRepository orderBookRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final BookManagementService bookManagementService;

    @Override
    public List<OrderBookResponseDto> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos) {
        List<OrderBook> orderBooks = new ArrayList<>();

        for (OrderBookRequestDto dto : orderBookRequestDtos) {
            Book book = bookRepository.findById(dto.getBookId()).orElseThrow(
                    () -> new NotFoundException("Book is out of stock or does not exist")
            );

            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException("Order not found for ID: " + dto.getOrderId()));

            try {
                bookManagementService.modifyQuantity(book.getBookId(), -dto.getQuantity());
            } catch (BookOutOfStockException e) {
                throw new NotFoundException("Not enough stock for book ID: " + book.getBookId());
            }

            OrderBook orderBook = dto.toEntity(book, order);
            orderBooks.add(orderBook);
        }

        List<OrderBook> savedOrderBooks = orderBookRepository.saveAll(orderBooks);

        return savedOrderBooks.stream()
                .map(this::toOrderBookResponseDto)
                .collect(Collectors.toList());
    }


    @Override
    public OrderBookResponseDto getOrderBook(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new IllegalArgumentException("OrderBook not found for ID: " + orderBookId));

        return toOrderBookResponseDto(orderBook);
    }

    @Override
    public OrderBookResponseDto updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new IllegalArgumentException("OrderBook not found for ID: " + orderBookId));

        orderBook.updateOrderBookState(newOrderBookState);

        OrderBook updatedOrderBook = orderBookRepository.save(orderBook);
        return toOrderBookResponseDto(updatedOrderBook);
    }

    @Override
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

    private OrderBookResponseDto toOrderBookResponseDto(OrderBook orderBook) {
        List<PackageResponseDto> packageDtos = orderBook.getPackages().stream()
                .map(this::toPackageResponseDto)
                .collect(Collectors.toList());

        CouponDiscountResponseDto couponDiscountDto = null;
        if (orderBook.getCouponDiscount() != null) {
            couponDiscountDto = toCouponDiscountResponseDto(orderBook.getCouponDiscount());
        }

        return OrderBookResponseDto.builder()
                .orderBookId(orderBook.getOrderBookId())
                .bookTitle(orderBook.getBook().getTitle())
                .quantity(orderBook.getQuantity())
                .salePrice(orderBook.getSalePrice())
                .discountPrice(orderBook.getDiscountPrice())
                .orderBookState(orderBook.getOrderBookState().name())
                .packages(packageDtos)
                .couponDiscount(couponDiscountDto)
                .build();
    }
}


