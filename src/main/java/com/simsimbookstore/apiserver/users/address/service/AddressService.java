package com.simsimbookstore.apiserver.users.address.service;

import com.simsimbookstore.apiserver.users.address.dto.AddressRequestDto;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.entity.Address;

import java.util.List;

public interface AddressService {
    // 주소 단건 조회
    Address getAddress(Long addressId);

    // 유저 아이디 기준 리스트 조회
    List<AddressResponseDto> getAddresses(Long userId);

    // 유저 저장
    AddressResponseDto createAddress(Long userId, AddressRequestDto addressRequestDto);

    // 주소 삭제
    void deleteAddress(Long addressId);

    int getCountAddresses(Long userId);
}
