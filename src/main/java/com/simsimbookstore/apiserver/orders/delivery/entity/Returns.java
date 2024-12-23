package com.simsimbookstore.apiserver.orders.delivery.entity;


import com.simsimbookstore.apiserver.orders.delivery.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "returns")
@Entity
public class Returns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @Column(name = "return_reason")
    private String returnReason;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "return_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private String returnState;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "refund", nullable = false)
    private Boolean refund;

    @Column(name = "damaged", nullable = false)
    private Boolean damaged;

    @OneToOne
    private Delivery delivery;

    @OneToOne
    private OrderBook orderBook;


    public enum ReturnState {
        RETURN_REQUESTED,         // 반품요청
        RETURN_APPROVED,          // 반품승인
        RETURN_REJECTED,          // 반품거절
        RETURN_SHIPPED_BACK,      // 반송
        RETURN_ITEM_INSPECTING,   // 반송상품확인중
        RETURN_COMPLETED,         // 반품완료
        REFUND_COMPLETED          // 환불완료
    }

    public ReturnsResponseDto toResponseDto() {
        return ReturnsResponseDto.builder()
                .returnId(this.returnId)
                .returnReason(this.returnReason)
                .returnDate(this.returnDate)
                .returnState(this.returnState)
                .quantity(this.quantity)
                .refund(this.refund)
                .damaged(this.damaged)
                .bookId(this.orderBook != null && this.orderBook.getBook() != null ? this.orderBook.getBook().getBookId() : null)
                .bookTitle(this.orderBook != null && this.orderBook.getBook() != null ? this.orderBook.getBook().getTitle() : null)
                .build();
    }

    public void updateReturnState(ReturnState newState) {
        this.returnState = newState.name();
    }

}
