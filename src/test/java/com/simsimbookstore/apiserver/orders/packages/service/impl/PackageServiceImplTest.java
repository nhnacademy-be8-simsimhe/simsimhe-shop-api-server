package com.simsimbookstore.apiserver.orders.packages.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.exception.PackagesNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.exception.WrapTypeNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.repository.PackagesRepository;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;



@ExtendWith(MockitoExtension.class)
class PackageServiceImplTest {

    @Mock
    private PackagesRepository packagesRepository;


    @Mock
    private WrapTypeRepository wrapTypeRepository;

    @InjectMocks
    private PackageServiceImpl packageService; // 테스트 대상(Service 구현체)

    private OrderBook orderBook;
    private WrapType wrapType;

    @BeforeEach
    void setUp() {
        // 예시 도메인 객체 세팅
        orderBook = OrderBook.builder()
                .orderBookId(100L)
                .build();

        wrapType = WrapType.builder()
                .packageTypeId(10L)
                .packageName("GIFT")
                .build();
    }

    @Test
    void createPackage_Success() {
        // when
        PackageRequestDto dto = PackageRequestDto.builder()
                .packageTypeId(10L)
                .packageName("GiftBox")
                .build();

       // when(orderBookRepository.findById(100L)).thenReturn(java.util.Optional.of(orderBook));
        when(wrapTypeRepository.findById(10L)).thenReturn(java.util.Optional.of(wrapType));

        Packages savedPackage = Packages.builder()
                .packageId(1L)
                .packageType("GiftBox")
                .wrapType(wrapType)
                .orderBook(orderBook)
                .build();

        // packageRepository.save(...) 호출 시 mock 결과 지정
        when(packagesRepository.save(any(Packages.class))).thenReturn(savedPackage);

        // when
        Packages result = packageService.createPackage(dto, orderBook);

        // then
        assertNotNull(result);
        assertEquals("GiftBox", result.getPackageType());
        assertEquals(orderBook, result.getOrderBook());
        assertEquals(wrapType, result.getWrapType());

        // 검증: 저장 로직이 실제로 호출되었는지
        verify(packagesRepository, times(1)).save(any(Packages.class));
    }

    @Test
    void createPackage_WrapTypeNotFound() {
        PackageRequestDto dto = PackageRequestDto.builder()
                .packageTypeId(999L) // 존재하지 않는 wrapTypeId
                .packageName("UnknownBox")
                .build();
        when(wrapTypeRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(WrapTypeNotFoundException.class,
                () -> packageService.createPackage(dto, orderBook));
    }

    @Test
    void getPackageById_Success() {
        // when
        Packages pkg = Packages.builder().packageId(1L).packageType("Ribbon").build();
        when(packagesRepository.findById(1L)).thenReturn(java.util.Optional.of(pkg));

        // when
        Packages result = packageService.getPackageById(1L);

        // then
        assertNotNull(result);
        assertEquals("Ribbon", result.getPackageType());
        verify(packagesRepository, times(1)).findById(1L);
    }

    @Test
    void getPackageById_NotFound() {
        // when
        when(packagesRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // when & then
        assertThrows(PackagesNotFoundException.class,
                () -> packageService.getPackageById(999L));
    }
}

