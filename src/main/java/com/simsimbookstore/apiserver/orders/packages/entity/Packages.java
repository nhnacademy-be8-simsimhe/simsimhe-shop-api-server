package com.simsimbookstore.apiserver.orders.packages.entity;


import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "packages")
public class Packages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;


    @Column(name = "package_type", nullable = false, length = 20)
    private String packageType;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "order_book_id")
    private OrderBook orderBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wrap_type_id")
    private WrapType wrapType;


    public Packages updatedPackage(OrderBook orderBook, WrapType wrapType, String packageType) {
        return Packages.builder()
                .packageId(this.packageId)
                .orderBook(orderBook)
                .wrapType(wrapType)
                .packageType(packageType)
                .build();
    }
}
