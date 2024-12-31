package com.simsimbookstore.apiserver.users.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.dto.UserGradeUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserStatusUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    User testUser;
    Grade testGrade;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        testUser = User.builder()
                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .build();


        when(userService.updateUserStatus(anyLong(), any(UserStatus.class))).thenReturn(testUser);
    }

    @Test
    @DisplayName("유저 상태 업데이트")
    void updateStatus() throws Exception {

        Long UserId = 1L;
        UserStatusUpdateRequestDto userStatusUpdateRequestDto = UserStatusUpdateRequestDto.builder()
                .status(UserStatus.ACTIVE)
                .build();

        testUser.updateUserStatus(UserStatus.INACTIVE);
        when(userService.updateUserStatus(testUser.getUserId(), UserStatus.INACTIVE)).thenReturn(testUser);


        mockMvc.perform(put("/api/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userStatusUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(UserId))
                .andExpect(jsonPath("$.userStatus").value(UserStatus.INACTIVE.toString()));
    }

    @Test
    @DisplayName("유저 등급 업데이트")
    void updateGrade() throws Exception {
        Long userId = 1L;

        UserGradeUpdateRequestDto userGradeUpdateRequestDto = UserGradeUpdateRequestDto.builder()
                .tier(Tier.ROYAL)
                .build();

        testGrade.setTier(Tier.ROYAL);

        testUser.updateGrade(testGrade);
        when(userService.updateUserGrade(userId, Tier.ROYAL)).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1/grade")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userGradeUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.grade.tier").value(Tier.ROYAL.toString()));
    }
}