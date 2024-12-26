package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.exception.BookOutOfStockException;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.exception.OrderNotFoundException;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<OrderBook> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos) {
        List<OrderBook> orderBooks = new ArrayList<>();

        for (OrderBookRequestDto dto : orderBookRequestDtos) {

            Book book = bookRepository.findByBookIdAndQuantityGreaterThan(dto.getBookId(), 0).orElseThrow(
                    () -> new BookOutOfStockException("Book is out of stock or does not exist")
            );

            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException("Order not found for ID: " + dto.getOrderId()));

            int updatedQuantity = book.getQuantity() - dto.getQuantity();
            if (updatedQuantity < 0) {
                throw new BookOutOfStockException("Not enough stock for book ID: " + book.getBookId());
            }
            book.setQuantity(updatedQuantity);

            bookRepository.save(book);


            OrderBook orderBook = dto.toEntity(book, order);
            orderBooks.add(orderBook);
        }

        return orderBookRepository.saveAll(orderBooks);
    }

    @Override
    public OrderBook getOrderBook(Long orderBookId) {
        return orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new IllegalArgumentException("OrderBook not found for ID: " + orderBookId));
    }

    @Override
    public OrderBook updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState) {

        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new IllegalArgumentException("OrderBook not found for ID: " + orderBookId));

        orderBook.updateOrderBookState(newOrderBookState);

        return orderBookRepository.save(orderBook);
    }

    @Override
    public void deleteOrderBook(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found for ID: " + orderBookId));
        orderBookRepository.delete(orderBook);
    }

    @Override
    public List<Packages> getPackagesByOrderBookId(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found"));

        return orderBook.getPackages();
    }
}
