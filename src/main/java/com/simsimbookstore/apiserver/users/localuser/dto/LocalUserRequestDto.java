package com.simsimbookstore.apiserver.users.localuser.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class LocalUserRequestDto {

    @NotBlank
    @Length(min = 3, max = 50)
    private String userName;

    @NotBlank
    @Length(min = 8, max = 15)
    @Pattern(regexp = "^[0-9]+$",message = "핸드폰 번호는 숫자만 입력 가능합니다")
    private String mobileNumber;

    @NotBlank
    @Email(message = "이메일 형식이 아닙니다")
    @Length(max = 50)
    private String email;

    @NotNull
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate birth;

    @NotNull
    private Gender gender;

    private UserStatus userStatus = UserStatus.ACTIVE;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime latestLoginDate;

    private Tier tier = Tier.STANDARD;

    @NotNull
    private RoleName roleName;

    @NotNull
    @NotBlank
    private String loginId;

    @NotNull
    @NotBlank
    private String password;
}
