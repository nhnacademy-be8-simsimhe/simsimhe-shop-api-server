package com.simsimbookstore.apiserver.users.localuser.mapper;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserResponseDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LocalUserMapper {

    public static LocalUser registerRequestDtoTo(LocalUserRegisterRequestDto requestDto) {
        LocalUser localUser = LocalUser.builder()
                .userName(requestDto.getUserName())
                .mobileNumber(requestDto.getMobileNumber())
                .email(requestDto.getEmail())
                .birth(requestDto.getBirth())
                .gender(requestDto.getGender())
                .userStatus(requestDto.getUserStatus()) // 기본값 active
                .createdAt(LocalDateTime.now())
                .isSocialLogin(false)
                .password(requestDto.getPassword())
                .loginId(requestDto.getLoginId())
                .userRoleList(new HashSet<>())
                .latestLoginDate(LocalDateTime.now())
                .build();
        return localUser;
    }

    public static LocalUserResponseDto localUserResponseDtoTo(LocalUser localUser) {
        List<RoleName> roles = new ArrayList<>();

        for (UserRole userRole : localUser.getUserRoleList()){
            roles.add(userRole.getRole().getRoleName());
        }

        LocalUserResponseDto localUserResponseDto = LocalUserResponseDto.builder()
                .userId(localUser.getUserId())
                .loginId(localUser.getLoginId())
                .roles(roles)
                .password(localUser.getPassword())
                .userStatus(localUser.getUserStatus())
                .latestLoginDate(localUser.getLatestLoginDate())
                .build();

        return localUserResponseDto;
    }
}
