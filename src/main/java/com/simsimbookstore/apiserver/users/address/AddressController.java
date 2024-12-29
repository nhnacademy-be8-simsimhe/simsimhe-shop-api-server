package com.simsimbookstore.apiserver.users.address;

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
    public ResponseEntity<?> getAddress(
            @PathVariable Long addressId
    ){
        Address address = addressService.getAddress(addressId);
        return ResponseEntity.ok(address);
    }

    // 리스트 조회
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<?> getAddresses(
            @PathVariable Long userId
    ){
        List<AddressResponseDto> addresses = addressService.getAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    // 주소 등록
    @PostMapping("{userId}/addresses")
    public ResponseEntity<?> createAddress(
            @PathVariable Long userId,
            @RequestBody @Valid AddressRequestDto requestDto
    ) {
        AddressResponseDto address = addressService.createAddress(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    // 주소 삭제
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long addressId
    ){
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }
}
