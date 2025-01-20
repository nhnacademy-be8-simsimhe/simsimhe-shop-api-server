package com.simsimbookstore.apiserver.users.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.users.UserMapper;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.dto.UserGradeUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserLatestLoginDateUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserResponse;
import com.simsimbookstore.apiserver.users.user.dto.UserStatusUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.service.impl.UserServiceImpl;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.hamcrest.Matchers;
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
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .gender(Gender.FEMALE)
                .userRoleList(new HashSet<>())
                .build();

        UserRole userRole = UserRole.builder()
                .userRoleId(1L)
                .user(testUser)
                .role(new Role(1L, RoleName.USER))
                .build();

        testUser.addUserRole(userRole);


        when(userService.updateUserStatus(anyLong(), any(UserStatus.class))).thenReturn(testUser);
    }

    @Test
    @DisplayName("유저 상태 업데이트")
    void updateStatus() throws Exception {

        UserStatusUpdateRequestDto userStatusUpdateRequestDto = UserStatusUpdateRequestDto.builder()
                .status(UserStatus.ACTIVE)
                .build();

        testUser.updateUserStatus(UserStatus.INACTIVE);
        when(userService.updateUserStatus(testUser.getUserId(), UserStatus.INACTIVE)).thenReturn(testUser);


        mockMvc.perform(put("/api/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userStatusUpdateRequestDto)))
                .andExpect(status().isOk());
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

    @Test
    @DisplayName("유저 최근 로그인 날짜 업데이트")
    void updateLatestLoginDate() throws Exception {
        Long userId = 1L;
        LocalDateTime latestLoginDate = LocalDateTime.now();
        UserLatestLoginDateUpdateRequestDto requestDto = UserLatestLoginDateUpdateRequestDto.builder()
                .latestLoginDate(latestLoginDate)
                .build();

        testUser.updateLatestLoginDate(latestLoginDate);

        when(userService.updateUserLatestLoginDate(userId, requestDto.getLatestLoginDate())).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1/latestLoginDate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
//                .andExpect(jsonPath("$.latestLoginDate").value(latestLoginDate.toString()));
    }

    @Test
    void getUser() throws Exception {
        Long userId = 1L;
        when(userService.getUserWithGradeAndRoles(userId)).thenReturn(testUser);

        UserResponse response = UserMapper.toResponse(testUser);
        mockMvc.perform(get("/api/users/{userId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userName").value(testUser.getUserName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.userStatus").value(testUser.getUserStatus().toString()))
                .andExpect(jsonPath("$.tier").value(testUser.getGrade().getTier().toString()))
                .andExpect(jsonPath("$.gender").value(testUser.getGender().toString()))
                .andExpect(jsonPath("$.roles", Matchers.hasItem(RoleName.USER.toString())));
    }

    @Test
    void getActiveUser() throws Exception {
        UserResponse response = UserMapper.toResponse(testUser);
        when(userService.getAllActiveUser()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users/active")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(response))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(1)));

//        @GetMapping("/active")
//    public ResponseEntity<List<UserResponse>> getActiveUser() {
//        List<UserResponse> allActiveUser = userService.getAllActiveUser();
//        return ResponseEntity.status(HttpStatus.OK).body(allActiveUser);
//    }
    }
}