package com.simsimbookstore.apiserver.users.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class GuestUserRequestDto {
    private String userName;
    private String mobileNumber;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private Gender gender;
}
