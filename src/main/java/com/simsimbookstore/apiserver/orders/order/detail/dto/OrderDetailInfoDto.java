package com.simsimbookstore.apiserver.orders.order.detail.dto;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderDetailInfoDto {
    // order
    private String orderNumber;
    private LocalDateTime orderDate;
    private Order.OrderState orderState;
    private String senderName;
    private String senderPhoneNumber;
    private String senderEmail;
    private int pointEarn;
    private BigDecimal pointUse;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal originalTotalPrice;

    // delivery
    private String receiverName;
    private String receiverPhoneNumber;
    private Integer trackingNumber;
    private String postalCode;
    private String roadAddress;
    private String detailedAddress;
    private String reference;
    private Delivery.DeliveryState deliveryState;

    // payment_method
    private String paymentMethod;

    public OrderDetailInfoDto(Order order, Delivery delivery, Payment payment) {
        this.orderNumber = order.getOrderNumber();
        this.orderDate = order.getOrderDate();
        this.orderState = order.getOrderState();
        this.senderName = order.getSenderName();
        this.senderPhoneNumber = order.getPhoneNumber();
        this.senderEmail = order.getOrderEmail();
        this.pointEarn = order.getPointEarn();
        this.pointUse = order.getPointUse();
        this.totalPrice = order.getTotalPrice();
        this.deliveryPrice = order.getDeliveryPrice();
        this.originalTotalPrice = order.getOriginalPrice();

        this.receiverName = delivery.getDeliveryReceiver();
        this.receiverPhoneNumber = delivery.getReceiverPhoneNumber();
        this.trackingNumber = delivery.getTrackingNumber();
        this.postalCode = delivery.getPostalCode();
        this.roadAddress = delivery.getRoadAddress();
        this.detailedAddress = delivery.getDetailedAddress();
        this.reference = delivery.getReference();
        this.deliveryState = delivery.getDeliveryState();

        this.paymentMethod = payment.getTossReturnMethod();
    }
}
