package com.simsimbookstore.apiserver.users.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class UserResponse {

    private Long userId;

    private String userName;

    private String mobileNumber;

    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private Gender gender;

    private UserStatus userStatus;

    private LocalDateTime createdAt;

    private LocalDateTime latestLoginDate;

    private boolean isSocialLogin;

    private Tier tier;

    private List<RoleName> roles;
}
