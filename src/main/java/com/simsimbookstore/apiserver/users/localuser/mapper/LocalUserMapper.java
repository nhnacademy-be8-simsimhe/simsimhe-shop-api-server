package com.simsimbookstore.apiserver.users.localuser.mapper;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;

import java.time.LocalDateTime;
import java.util.HashSet;

public class LocalUserMapper {

    public static LocalUser requestDtoTo(LocalUserRequestDto requestDto) {
        LocalUser localUser = LocalUser.builder()
                .userName(requestDto.getUserName())
                .mobileNumber(requestDto.getMobileNumber())
                .email(requestDto.getEmail())
                .birth(requestDto.getBirth())
                .gender(requestDto.getGender())
                .userStatus(requestDto.getUserStatus())
                .createdAt(LocalDateTime.now())
                .isSocialLogin(false)
                .password(requestDto.getPassword())
                .loginId(requestDto.getLoginId())
                .userRoleList(new HashSet<>())
                .build();
        return localUser;
    }
}
