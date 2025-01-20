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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    // 주소 단건 조회
    @Override
    public Address getAddress(Long addressId) {

        return addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Not founded address with ID: " + addressId));
    }

    // 유저 아이디 기준 리스트 조회
    @Override
    public List<AddressResponseDto> getAddresses(Long userId) {
        List<Address> addresses = addressRepository.findAllByUserUserId(userId);
        return addresses.stream()
                .map(AddressMapper::responseDtoFrom)
                .toList();
    }

    // 유저 저장
    @Transactional
    @Override
    public AddressResponseDto createAddress(Long userId, AddressRequestDto addressRequestDto) {
        Address address = AddressMapper.requestDtoTo(addressRequestDto);
        User user = userService.getUser(userId);
        ;

        address.assignUser(user);
        Address savedAddress = addressRepository.save(address);
        return AddressMapper.responseDtoFrom(savedAddress);
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

    @Override
    public int getCountAddresses(Long userId) {
        return addressRepository.countAllByUserUserId(userId);
    }
}
