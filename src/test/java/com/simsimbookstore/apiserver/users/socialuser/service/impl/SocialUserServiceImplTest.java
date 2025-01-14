package com.simsimbookstore.apiserver.users.socialuser.service.impl;

import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import com.simsimbookstore.apiserver.users.socialuser.dto.Provider;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserRequestDto;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import com.simsimbookstore.apiserver.users.socialuser.mapper.SocialUserMapper;
import com.simsimbookstore.apiserver.users.socialuser.repository.SocialUserRepository;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SocialUserServiceImplTest {

    @InjectMocks
    private SocialUserServiceImpl socialUserService;

    @Mock
    private SocialUserRepository socialUserRepository;

    @Mock
    private GradeService gradeService;

    @Mock
    private RoleService roleService;


    SocialUserRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = SocialUserRequestDto.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .mobile("01000000000")
                .gender(Gender.MALE)
                .oauthId("testOauthId")
                .provider(Provider.PAYCO)
                .build();
    }

    @Test
    @DisplayName("socialUser 정보가 이미 있는 경우")
    void loginSocialUser_getSocialUser() {

        SocialUser testSocialUser = SocialUserMapper.toSocialUser(requestDto);
        when(socialUserRepository.existsByOauthId(requestDto.getOauthId())).thenReturn(Boolean.TRUE);
        when(socialUserRepository.findByOauthId(requestDto.getOauthId())).thenReturn(Optional.of(testSocialUser));

        SocialUser socialUser = socialUserService.loginSocialUser(requestDto);
        verify(socialUserRepository, times(1)).existsByOauthId(requestDto.getOauthId());
        verify(socialUserRepository, times(1)).findByOauthId(requestDto.getOauthId());

        assertEquals(requestDto.getOauthId(), socialUser.getOauthId());
    }

    @Test
    @DisplayName("소셜 유저 정보가 없는 경우 등록")
    void loginSocialUser_registerSocialUser() {
        // given
        SocialUser testSocialUser = SocialUser.builder()
                .oauthId(requestDto.getOauthId())
                .userName("John Doe")
                .email("johndoe@example.com")
                .build();

        when(socialUserRepository.existsByOauthId(requestDto.getOauthId())).thenReturn(Boolean.FALSE);

        when(socialUserRepository.save(any(SocialUser.class))).thenReturn(testSocialUser);

        SocialUser socialUser = socialUserService.loginSocialUser(requestDto);

        verify(socialUserRepository, times(1)).existsByOauthId(requestDto.getOauthId());
        verify(socialUserRepository, times(1)).save(any(SocialUser.class));

        assertNotNull(socialUser);
        assertEquals(requestDto.getOauthId(), socialUser.getOauthId());
        assertEquals("John Doe", socialUser.getUserName());
    }

}