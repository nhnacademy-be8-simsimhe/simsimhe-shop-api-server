package com.simsimbookstore.apiserver.orders.order.detail.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.entity.QOrderBook;
import com.simsimbookstore.apiserver.orders.packages.entity.QPackages;
import com.simsimbookstore.apiserver.orders.packages.entity.QWrapType;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderBookQueryRepository extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public OrderBookQueryRepository(EntityManager entityManager) {
        super(OrderBook.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<WrapType> findByPackage(OrderBook orderBook) {
        QOrderBook qOrderBook = QOrderBook.orderBook;
        QPackages packages = QPackages.packages;
        QWrapType wrapType = QWrapType.wrapType;

        return queryFactory
                .select(wrapType)
                .from(qOrderBook)
                .join(qOrderBook.packages, packages)
                .join(packages.wrapType, wrapType)
                .fetch();
    }
}
