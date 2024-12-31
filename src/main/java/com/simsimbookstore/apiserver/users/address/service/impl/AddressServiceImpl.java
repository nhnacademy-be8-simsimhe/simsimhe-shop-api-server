package com.simsimbookstore.apiserver.users.address.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.address.dto.AddressRequestDto;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.address.mapper.AddressMapper;
import com.simsimbookstore.apiserver.users.address.repository.AddressRepository;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    // 주소 단건 조회
    @Override
    public Address getAddress(Long addressId){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Not founded address with ID: " + addressId));

        return address;
    }

    // 유저 아이디 기준 리스트 조회
    @Override
    public List<Address> getAddresses(Long userId) {
        addressRepository.findAllByUserUserId(userId);
        return addressRepository.findAll();
    }

    // 유저 저장
    @Transactional
    @Override
    public AddressResponseDto createAddress(Long userId, AddressRequestDto addressRequestDto) {
        Address address = AddressMapper.requestDtoTo(addressRequestDto);
        User user = userService.getUser(userId);;

        address.assignUser(user);
        Address savedAddress = addressRepository.save(address);
        AddressResponseDto addressResponseDto = AddressMapper.responseDtoFrom(savedAddress);
        return addressResponseDto;
    }

    // 주소 삭제
    @Transactional
    @Override
    public void deleteAddress(Long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new NotFoundException("Not founded address with ID: " + addressId);
        }
        addressRepository.deleteById(addressId);
    }
}
