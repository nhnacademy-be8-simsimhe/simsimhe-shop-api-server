package com.simsimbookstore.apiserver.users.address.controller;

import com.simsimbookstore.apiserver.users.address.dto.AddressRequestDto;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // 단건 조회
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<Address> getAddress(
            @PathVariable Long addressId
    ) {
        Address address = addressService.getAddress(addressId);
        return ResponseEntity.ok(address);
    }

    // 리스트 조회
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDto>> getAddresses(
            @PathVariable Long userId
    ) {
        List<AddressResponseDto> addresses = addressService.getAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    // 주소 등록
    @PostMapping("{userId}/addresses")
    public ResponseEntity<Object> addAddress(
            @PathVariable Long userId,
            @RequestBody @Valid AddressRequestDto requestDto
    ) {
        int countAddresses = addressService.getCountAddresses(userId);

        if (countAddresses >= 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최대 10개의 주소만 등록할 수 있습니다.");
        }

        AddressResponseDto address = addressService.createAddress(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    // 주소 삭제
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId
    ) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }
}
