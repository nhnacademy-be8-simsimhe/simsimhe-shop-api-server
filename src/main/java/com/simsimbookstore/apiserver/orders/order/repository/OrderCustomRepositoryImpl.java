package com.simsimbookstore.apiserver.orders.order.repository;

import static com.simsimbookstore.apiserver.orders.delivery.entity.QDelivery.delivery;
import static com.simsimbookstore.apiserver.orders.orderbook.entity.QOrderBook.orderBook;
import static com.simsimbookstore.apiserver.orders.order.entity.QOrder.order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findAllOrdersNeedToComplete(LocalDate thirtyDaysAgo) {
        return queryFactory
                .selectFrom(order)
                .leftJoin(order.delivery, delivery).fetchJoin()
                .leftJoin(order.orderBooks, orderBook).fetchJoin()
                .where(
                        order.orderState.ne(Order.OrderState.COMPLETED),
                        order.deliveryDate.loe(thirtyDaysAgo)
                )
                .distinct()
                .fetch();
    }
}
