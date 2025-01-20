package com.simsimbookstore.apiserver.users.socialuser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.socialuser.dto.Provider;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserRequestDto;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserResponse;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import com.simsimbookstore.apiserver.users.socialuser.mapper.SocialUserMapper;
import com.simsimbookstore.apiserver.users.socialuser.service.impl.SocialUserServiceImpl;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialUserController.class)
@ExtendWith(MockitoExtension.class)
class SocialUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SocialUserServiceImpl socialUserService;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    private SocialUser testSocialUser;

    @BeforeEach
    void setUp() {
        Grade testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        UserRole testUserRole = UserRole.builder()
                .userRoleId(1L)
                .user(testSocialUser)
                .role(Role.builder().roleId(1L).roleName(RoleName.USER).build())
                .build();

        testSocialUser = SocialUser.builder()
                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .oauthId("testOauthId")
                .provider(Provider.PAYCO)
                .build();

        testSocialUser.addUserRole(testUserRole);
    }

    @Test
    void loginSocialUser() throws Exception {
        when(socialUserService.loginSocialUser(any(SocialUserRequestDto.class))).thenReturn(testSocialUser);

        SocialUserResponse response = SocialUserMapper.toSocialUserResponse(testSocialUser);

        mockMvc.perform(post("/api/users/socialUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testSocialUser.getUserId()))
                .andExpect(jsonPath("$.roles.size()").value(1))
                .andExpect(jsonPath("$.roles", Matchers.hasItem("USER")))
                .andExpect(jsonPath("$.userStatus").value(testSocialUser.getUserStatus().toString()))
                .andExpect(jsonPath("$.latestLoginDate").value(testSocialUser.getLatestLoginDate()))
                .andExpect(jsonPath("$.oauthId").value(testSocialUser.getOauthId()))
                .andExpect(jsonPath("$.provider").value(testSocialUser.getProvider().toString()));

    }
}