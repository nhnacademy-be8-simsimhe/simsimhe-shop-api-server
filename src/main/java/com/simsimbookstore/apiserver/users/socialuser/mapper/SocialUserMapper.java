package com.simsimbookstore.apiserver.users.socialuser.mapper;

import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.socialuser.dto.Provider;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserRequestDto;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserResponse;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SocialUserMapper {
    private SocialUserMapper() {
    }

    public static SocialUser toSocialUser(SocialUserRequestDto socialUserRequestDto) {
        return SocialUser.builder()
                .userName(socialUserRequestDto.getName())
                .mobileNumber(socialUserRequestDto.getMobile())
                .email(socialUserRequestDto.getEmail())
                .gender(socialUserRequestDto.getGender())
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .userRoleList(new HashSet<>())
                .isSocialLogin(true)
                .oauthId(socialUserRequestDto.getOauthId())
                .provider(Provider.PAYCO)
                .build();
    }

    public static SocialUserResponse toSocialUserResponse(SocialUser socialUser) {
        List<RoleName> roleNames = new ArrayList<>();
        for (UserRole userRole : socialUser.getUserRoleList()) {
            roleNames.add(userRole.getRole().getRoleName());
        }

        return SocialUserResponse.builder()
                .userId(socialUser.getUserId())
                .roles(roleNames)
                .userStatus(socialUser.getUserStatus())
                .latestLoginDate(socialUser.getLatestLoginDate())
                .oauthId(socialUser.getOauthId())
                .provider(socialUser.getProvider())
                .build();
    }
}
