package com.simsimbookstore.apiserver.users.address;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.users.address.dto.AddressRequestDto;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.address.mapper.AddressMapper;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    User testUser;
    Address testAddress1;
    Address testAddress2;

    AddressRequestDto testAddressRequestDto;
    AddressResponseDto testAddressResponseDto;
    @BeforeEach
    void setUp() {
        Grade testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        testUser = User.builder()
                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .build();

        testAddress1 = Address.builder()
                .addressId(1L)
                .user(testUser)
                .alias("자취방")
                .postalCode("61459")
                .roadAddress("광주광역시 동구 의재로 9")
                .detailedAddress("리더스빌 403호")
                .build();

        testAddress2 = Address.builder()
                .addressId(2L)
                .user(testUser)
                .alias("본집")
                .postalCode("21953")
                .roadAddress("인천광역시 연수구 청량로 210")
                .detailedAddress("쌍용아파트 102동 1602호")
                .build();

        testAddressRequestDto = AddressRequestDto.builder()
                .alias("본집")
                .postalCode("21953")
                .roadAddress("인천광역시 연수구 청량로 210")
                .detailedAddress("쌍용아파트 102동 1602호")
                .build();
    }

    @Test
    @DisplayName("단건 조회")
    void getAddress() throws Exception {
        AddressRequestDto addressRequestDto = AddressRequestDto.builder()
                .alias("본집")
                .postalCode("21953")
                .roadAddress("인천광역시 연수구 청량로 210")
                .detailedAddress("쌍용아파트 102동 1602호")
                .build();

        when(addressService.getAddress(1L)).thenReturn(testAddress1);
        mockMvc.perform(get("/api/users/addresses/{addressID}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(testAddress1.getAddressId()));
    }

    @Test
    @DisplayName("리스트 조회")
    void getAddresses() throws Exception {

        when(addressService.getAddresses(1L)).thenReturn(List.of(testAddress1,testAddress2));
        mockMvc.perform(get("/api/users/{userId}/addresses",1L)
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].addressId").value(Matchers.hasItems(1,2)))
                .andExpect(jsonPath("$[*].alias").value(Matchers.hasItems("본집","자취방")));
    }

    @Test
    void createAddress() throws Exception {
        AddressResponseDto addressResponseDto = AddressMapper.responseDtoFrom(testAddress1);

        when(addressService.createAddress(anyLong(), any(AddressRequestDto.class))).thenReturn(addressResponseDto);
        mockMvc.perform(post("/api/users/{userId}/addresses",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddressRequestDto)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressId").value(testAddress1.getAddressId()));
    }

    @Test
    void deleteAddress() throws Exception {
        mockMvc.perform(delete("/api/users/addresses/{addressId}", 1L))
                .andExpect(status().isOk());
    }
}