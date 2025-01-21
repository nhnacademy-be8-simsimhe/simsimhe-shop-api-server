package com.simsimbookstore.apiserver.orders.order.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.time.LocalDate;
import java.util.List;

public interface OrderCustomRepository {
    List<Order> findAllOrdersNeedToComplete(LocalDate thirtyDaysAgo);

}
