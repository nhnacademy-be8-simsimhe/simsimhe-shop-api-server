package com.simsimbookstore.apiserver.users.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestUserRequestDto {
    private String userName;
    private String mobileNumber;
}
