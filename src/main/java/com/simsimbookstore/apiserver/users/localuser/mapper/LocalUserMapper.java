package com.simsimbookstore.apiserver.users.localuser.mapper;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.HashSet;

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
                .build();
        return localUser;
    }
}
