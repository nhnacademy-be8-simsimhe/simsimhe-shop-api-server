package com.simsimbookstore.apiserver.users.address.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.address.dto.AddressRequestDto;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.address.repository.AddressRepository;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserServiceImpl userService;

    User testUser;
    Address testAddress1;
    Address testAddress2;

    @BeforeEach
    void setUp() {
        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .pointRate(BigDecimal.valueOf(0.01))
                .build();

        testUser = User.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .build();

        testAddress1 = Address.builder()
                .user(testUser)
                .alias("자취방")
                .postalCode("61459")
                .roadAddress("광주광역시 동구 의재로 9")
                .detailedAddress("리더스빌 403호")
                .build();

        testAddress2 = Address.builder()
                .user(testUser)
                .alias("본집")
                .postalCode("21953")
                .roadAddress("인천광역시 연수구 청량로 210")
                .detailedAddress("쌍용아파트 102동 1602호")
                .build();
    }

    @Test
    @DisplayName("주소 단건 조회 성공")
    void getAddress() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress1));
        addressService.getAddress(1L);
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("주소 단건 조회 실패 - NotFoundException")
    void getAddressNotFound(){
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> addressService.getAddress(1L));
    }

    @Test
    @DisplayName("주소 리스트 조회")
    void getAddresses() {
        addressService.getAddresses(testUser.getUserId());
        verify(addressRepository, times(1)).findAllByUserUserId(testUser.getUserId());
    }

    @Test
    @DisplayName("주소 등록 성공")
    void createAddress() {
        AddressRequestDto addressRequestDto = AddressRequestDto.builder()
                .alias("본집")
                .postalCode("21953")
                .roadAddress("인천광역시 연수구 청량로 210")
                .detailedAddress("쌍용아파트 102동 1602호")
                .build();

        addressService.createAddress(testUser.getUserId(), addressRequestDto);
        verify(addressRepository, times(1)).save(any(Address.class));
    }


    @Test
    @DisplayName("주소 삭제 성공")
    void deleteAddress() {
        when(addressRepository.existsById(anyLong())).thenReturn(true);

        addressService.deleteAddress(1L);
        verify(addressRepository, times(1)).existsById(1L);
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("주소 삭제 실패 - NotFoundException")
    void deleteAddressNotFound(){
        when(addressRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> addressService.deleteAddress(1L));
    }


}