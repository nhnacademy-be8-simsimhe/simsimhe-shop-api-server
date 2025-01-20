package com.simsimbookstore.apiserver.orders.order.detail.dto;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailProduct {
    private String bookTitle;
    private String couponName;
    private BigDecimal couponPrice;
    private int quantity;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private String packageName;
    private BigDecimal packagePrice;
    private OrderBook.OrderBookState orderBookState;

    //String packageName, BigDecimal packagePrice
    public OrderDetailProduct(OrderBook orderBooks, String bookTitle, String couponName, BigDecimal couponPrice, String packageName, BigDecimal packagePrice, OrderBook.OrderBookState orderBookState) {
        this.bookTitle = bookTitle;
        this.couponName = couponName;
        this.couponPrice = couponPrice;
        this.quantity = orderBooks.getQuantity();
        this.originalPrice = orderBooks.getSalePrice();
        this.discountPrice = orderBooks.getDiscountPrice();
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.orderBookState = orderBookState;
    }
}
