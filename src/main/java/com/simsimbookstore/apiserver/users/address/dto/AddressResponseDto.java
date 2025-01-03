package com.simsimbookstore.apiserver.users.address.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddressResponseDto {

    @NotNull
    private Long addressId;

    @NotNull
    private Long userId;

    @NotNull
    private String alias;

    @NotNull
    private String postalCode;

    @NotNull
    private String roadAddress;

    private String detailedAddress;
}
