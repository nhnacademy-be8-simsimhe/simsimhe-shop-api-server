package com.simsimbookstore.apiserver.users.address.mapper;

import com.simsimbookstore.apiserver.users.address.dto.AddressRequestDto;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.entity.Address;

public class AddressMapper {

    public static Address requestDtoTo(AddressRequestDto requestDto) {
        Address address = Address.builder()
                .alias(requestDto.getAlias())
                .postalCode(requestDto.getPostalCode())
                .roadAddress(requestDto.getRoadAddress())
                .detailedAddress(requestDto.getDetailedAddress())
                .build();
        return address;
    }

    public static AddressResponseDto responseDtoFrom(Address address) {
        AddressResponseDto addressResponseDto = AddressResponseDto.builder()
                .userId(address.getUser().getUserId())
                .addressId(address.getAddressId())
                .alias(address.getAlias())
                .postalCode(address.getPostalCode())
                .roadAddress(address.getRoadAddress())
                .detailedAddress(address.getDetailedAddress())
                .build();
        return addressResponseDto;
    }
}
