package com.simsimbookstore.apiserver.users.user.dto;

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
