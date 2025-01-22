package com.simsimbookstore.apiserver.orders.returns.service.impl;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsRequestDto;
import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.returns.entity.Returns;
import com.simsimbookstore.apiserver.orders.delivery.exception.ReturnsNotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.returns.repository.ReturnsRepository;
import com.simsimbookstore.apiserver.orders.returns.service.ReturnsService;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnsServiceImpl implements ReturnsService {

    private final ReturnsRepository returnsRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderBookRepository orderBookRepository;

    /**
     * 반품 생성
     */
    @Override
    public void createReturn(ReturnsRequestDto requestDto) {
        List<Long> deliveries = requestDto.getDeliveryId();
        List<Long> orderBookIds = requestDto.getOrderBookId();
        List<Integer> quantities = requestDto.getQuantity();

        for (int i = 0; i < orderBookIds.size(); i++) {
            Long deliveryId = deliveries.get(i);
            Long orderBookId = orderBookIds.get(i);
            Integer quantity = quantities.get(i);

                // 배송 및 주문 확인
                Delivery delivery = deliveryRepository.findById(deliveryId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 배송 정보가 존재하지 않습니다."));
                OrderBook orderBook = orderBookRepository.findById(orderBookId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 주문 정보가 존재하지 않습니다."));

                // 중복 확인: orderBook 기준
                boolean exists = returnsRepository.existsByOrderBook(orderBook);
                if (exists) {
                    throw new IllegalArgumentException("해당 주문에 대한 반품 기록이 이미 존재합니다.");
                }

                // 반품 엔티티 생성
                Returns returns = Returns.builder()
                        .returnReason(requestDto.getReturnReason())
                        .returnDate(requestDto.getReturnDate())
                        .returnState(requestDto.getReturnState())
                        .quantity(quantity)
                        .refund(requestDto.getRefund())
                        .damaged(requestDto.getDamaged())
                        .delivery(delivery)
                        .orderBook(orderBook)
                        .build();

                // 반품 저장
                returnsRepository.save(returns);
            }
        }

    /**
     * 반품 상태 업데이트
     */
    @Override
    public ReturnsResponseDto updateReturnStatus(Long returnId, Returns.ReturnState newState) {
        Returns returns = returnsRepository.findById(returnId)
                .orElseThrow(() -> new ReturnsNotFoundException("해당 반품 요청을 찾을 수 없습니다."));

        // 상태 업데이트 메서드 호출
        returns.updateReturnState(newState);

        // 저장 후 DTO로 변환
        returnsRepository.save(returns);
        return returns.toResponseDto();
    }


    /**
     * 반품 단일 조회
     */
    @Override
    public ReturnsResponseDto getReturnById(Long returnId) {
        Returns returns = returnsRepository.findById(returnId).orElseThrow(
                ()-> new ReturnsNotFoundException("return not found")
        );

        // 엔티티를 DTO로 변환하여 반환
        return returns.toResponseDto();
    }

    /**
     * 반품 삭제
     */
    @Override
    public void deleteReturn(Long returnId) {
        Returns returns = returnsRepository.findById(returnId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반품 요청을 찾을 수 없습니다."));

        returnsRepository.delete(returns);
    }
}
