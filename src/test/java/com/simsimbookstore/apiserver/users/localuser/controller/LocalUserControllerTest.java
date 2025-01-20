package com.simsimbookstore.apiserver.users.localuser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserResponseDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.mapper.LocalUserMapper;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocalUserController.class)
@ExtendWith(MockitoExtension.class)
class LocalUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocalUserService localUserService;

    @Autowired
    private ObjectMapper objectMapper;

    LocalUserRegisterRequestDto testUserRequestDto;

    LocalUser testLocalUser;

    @BeforeEach
    void setUp() {
        testUserRequestDto = LocalUserRegisterRequestDto.builder()
                .userName("John Doe")
                .mobileNumber("01051278121")
                .email("johndoe@example.com")
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .tier(Tier.STANDARD)
                .roleName(RoleName.USER)
                .loginId("test")
                .password("test")
                .build();

        UserRole testUserRole = UserRole.builder()
                .userRoleId(1L)
                .user(testLocalUser)
                .role(Role.builder().roleId(1L).roleName(RoleName.USER).build())
                .build();

        testLocalUser = LocalUser.builder()
                .userName(testUserRequestDto.getUserName())
                .mobileNumber(testUserRequestDto.getMobileNumber())
                .email(testUserRequestDto.getEmail())
                .birth(testUserRequestDto.getBirth())
                .gender(testUserRequestDto.getGender())
                .userStatus(UserStatus.ACTIVE)
                .userRoleList(Set.of(testUserRole))
                .createdAt(testUserRequestDto.getCreatedAt())
                .loginId(testUserRequestDto.getLoginId())
                .password(testUserRequestDto.getPassword())
                .latestLoginDate(LocalDateTime.now())
                .build();
    }

    @Test
    void addLocalUSer() throws Exception {
        when(localUserService.saveLocalUser(any(LocalUserRegisterRequestDto.class))).thenReturn(testLocalUser);

        mockMvc.perform(post("/api/users/localUsers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value(testUserRequestDto.getUserName()))
                .andExpect(jsonPath("$.mobileNumber").value(testUserRequestDto.getMobileNumber()))
                .andExpect(jsonPath("$.email").value(testUserRequestDto.getEmail()))
                .andExpect(jsonPath("$.birth").value(testUserRequestDto.getBirth().toString()))
                .andExpect(jsonPath("$.gender").value(testUserRequestDto.getGender().toString()))
                .andExpect(jsonPath("$.userStatus").value(testUserRequestDto.getUserStatus().toString()))
                .andExpect(jsonPath("$.loginId").value(testUserRequestDto.getLoginId()))
                .andExpect(jsonPath("$.password").value(testUserRequestDto.getPassword()));
    }

    @Test
    void existsByLoginIdExist() throws Exception {
        when(localUserService.existsByLoginId(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/users/localUsers/{loginId}/exists", "testLoginId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByLoginIdNotExist() throws Exception {
        when(localUserService.existsByLoginId(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/users/localUsers/{loginId}/exists", "testLoginId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getLocalUser() throws Exception {
        LocalUserResponseDto responseDto = LocalUserMapper.localUserResponseDtoTo(testLocalUser);
        when(localUserService.findByLoginId(anyString())).thenReturn(testLocalUser);

        mockMvc.perform(get("/api/users/localUsers/{loginId}", testLocalUser.getLoginId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(responseDto.getUserId()))
                .andExpect(jsonPath("$.loginId").value(responseDto.getLoginId()))
                .andExpect(jsonPath("$.password").value(responseDto.getPassword()))
                .andExpect(jsonPath("$.userStatus").value(responseDto.getUserStatus().toString()))
                .andExpect(jsonPath("$.latestLoginDate").exists()); // Nullable
    }
}