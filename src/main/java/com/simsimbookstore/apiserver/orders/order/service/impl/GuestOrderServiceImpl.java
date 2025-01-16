package com.simsimbookstore.apiserver.orders.order.service.impl;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.order.service.GuestOrderService;
import com.simsimbookstore.apiserver.users.user.dto.GuestUserRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class GuestOrderServiceImpl implements GuestOrderService {

    private final UserService userService;


    /**
     * 비회원 주문을 위한 비회원 생성
     *
     * @param //OrderFacadeRequsetDto
     * @return Guest userId
     */
    @Override
    public Long prepareUser(OrderFacadeRequestDto dto) {
        if (dto.getMemberOrderRequestDto().getUserId() == null) {
            GuestUserRequestDto guestDto = GuestUserRequestDto.builder()
                    .userName(dto.getMemberOrderRequestDto().getSenderName()).build();

            User guest = userService.createGuest(guestDto);
            return guest.getUserId();
        } else {
            return dto.getMemberOrderRequestDto().getUserId();
        }
    }
}
