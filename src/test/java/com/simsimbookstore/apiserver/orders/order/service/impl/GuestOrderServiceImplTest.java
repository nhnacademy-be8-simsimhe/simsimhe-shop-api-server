package com.simsimbookstore.apiserver.orders.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuestOrderServiceImplTest {

    @Mock
    private UserService userService;
    User user;
    private GuestOrderServiceImpl guestOrderService;

    @BeforeEach
    void setUp() {
        guestOrderService = new GuestOrderServiceImpl(userService);
    }

    @Test
    @DisplayName("prepareUser: 신규 게스트 생성 테스트 - userId가 null일 때")
    void testPrepareUser_createsGuest() {
        // given
        // 테스트용 게스트 사용자 생성 및 반환값 설정
        Long userId = 1L;
        user = User.builder().userId(userId).build();
        when(userService.createGuest(any())).thenReturn(user);

        // OrderFacadeRequestDto 및 하위 DTO 설정
        MemberOrderRequestDto memberDto = MemberOrderRequestDto.builder()
                .userId(null)
                .senderName("Test Sender")
                .build();

        OrderFacadeRequestDto orderDto = OrderFacadeRequestDto.builder()
                .memberOrderRequestDto(memberDto)
                .build();

        // when
        Long result = guestOrderService.prepareUser(orderDto);

        // then
        assertEquals(1L, result);
        // createGuest 메서드가 올바른 파라미터로 호출되었는지 검증
        verify(userService).createGuest(argThat(dto -> "Test Sender".equals(dto.getUserName())));
    }
}