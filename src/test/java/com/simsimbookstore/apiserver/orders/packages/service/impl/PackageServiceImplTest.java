package com.simsimbookstore.apiserver.orders.packages.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.exception.PackagesNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.exception.WrapTypeNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.repository.PackageRepository;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PackageServiceImplTest {

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private WrapTypeRepository wrapTypeRepository;

    @InjectMocks
    private PackageServiceImpl packageService;

    @Test
    @DisplayName("포장이 성공함")
    void createPackage_ShouldSavePackage_WhenValidRequest() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Test Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        OrderBook orderBook = new OrderBook();
        WrapType wrapType = new WrapType();

        when(orderBookRepository.findById(1L)).thenReturn(Optional.of(orderBook));
        when(wrapTypeRepository.findById(2L)).thenReturn(Optional.of(wrapType));

        Packages savedPackage = Packages.builder()
                .packageId(1L)
                .packageType("Test Package")
                .orderBook(orderBook)
                .wrapType(wrapType)
                .build();

        when(packageRepository.save(any(Packages.class))).thenReturn(savedPackage);

        Packages result = packageService.createPackage(requestDto);

        assertNotNull(result);
        assertEquals("Test Package", result.getPackageType());
        verify(packageRepository, times(1)).save(any(Packages.class));
    }

    @Test
    @DisplayName("포장id로 포장 찾기")
    void getPackageById_ShouldReturnPackage_WhenPackageExists() {
        Packages existingPackage = Packages.builder()
                .packageId(1L)
                .packageType("Test Package")
                .build();

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));

        Packages result = packageService.getPackageById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPackageId());
        verify(packageRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("포장 삭제 테스트")
    void deletePackage_ShouldCallDelete_WhenPackageExists() {
        // Given
        Packages existingPackage = Packages.builder()
                .packageId(1L)
                .build();

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));

        // When
        packageService.deletePackage(1L);

        // Then
        verify(packageRepository, times(1)).delete(existingPackage);
        verify(packageRepository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("포장 업데이트")
    void updatePackage_ShouldUpdatePackage_WhenValidRequest() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Updated Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        OrderBook orderBook = new OrderBook();
        WrapType wrapType = new WrapType();

        Packages existingPackage = Packages.builder()
                .packageId(1L)
                .packageType("Old Package")
                .orderBook(orderBook)
                .wrapType(wrapType)
                .build();

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));
        when(orderBookRepository.findById(1L)).thenReturn(Optional.of(orderBook));
        when(wrapTypeRepository.findById(2L)).thenReturn(Optional.of(wrapType));

        Packages updatedPackage = Packages.builder()
                .packageId(1L)
                .packageType("Updated Package")
                .orderBook(orderBook)
                .wrapType(wrapType)
                .build();

        when(packageRepository.save(any(Packages.class))).thenReturn(updatedPackage);

        Packages result = packageService.updatePackage(1L, requestDto);

        assertNotNull(result);
        assertEquals("Updated Package", result.getPackageType());
        verify(packageRepository, times(1)).save(any(Packages.class));
    }

    @Test
    @DisplayName("오더북이 없으면 예외 던짐")
    void createPackage_ShouldThrowException_WhenOrderBookNotFound() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Test Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        when(orderBookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderBookNotFoundException.class, () -> packageService.createPackage(requestDto));
        verify(orderBookRepository, times(1)).findById(1L);
        verify(wrapTypeRepository, never()).findById(any());
        verify(packageRepository, never()).save(any());
    }

    @Test
    @DisplayName("포장지 없으면 예외던짐")
    void createPackage_ShouldThrowException_WhenWrapTypeNotFound() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Test Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        OrderBook orderBook = new OrderBook();

        when(orderBookRepository.findById(1L)).thenReturn(Optional.of(orderBook));
        when(wrapTypeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(WrapTypeNotFoundException.class, () -> packageService.createPackage(requestDto));
        verify(orderBookRepository, times(1)).findById(1L);
        verify(wrapTypeRepository, times(1)).findById(2L);
        verify(packageRepository, never()).save(any());
    }

    @Test
    @DisplayName("포장을 찾았는데 포장이 없으면 오류생김")
    void getPackageById_ShouldThrowException_WhenPackageNotFound() {
        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PackagesNotFoundException.class, () -> packageService.getPackageById(1L));
        verify(packageRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("없는 포장을 삭제할때 예외 던짐")
    void deletePackage_ShouldThrowException_WhenPackageNotFound() {
        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PackagesNotFoundException.class, () -> packageService.deletePackage(1L));
        verify(packageRepository, times(1)).findById(1L);
        verify(packageRepository, never()).delete(any());
    }

    @Test
    @DisplayName("없는 포장을 업데이트할때 예외던짐")
    void updatePackage_ShouldThrowException_WhenPackageNotFound() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Updated Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PackagesNotFoundException.class, () -> packageService.updatePackage(1L, requestDto));
        verify(packageRepository, times(1)).findById(1L);
        verify(orderBookRepository, never()).findById(any());
        verify(wrapTypeRepository, never()).findById(any());
        verify(packageRepository, never()).save(any());
    }

    @Test
    @DisplayName("포장을 업데이트할때 오더북이 없으면 예외던짐")
    void updatePackage_ShouldThrowException_WhenOrderBookNotFound() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Updated Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        Packages existingPackage = Packages.builder()
                .packageId(1L)
                .packageType("Old Package")
                .build();

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));
        when(orderBookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderBookNotFoundException.class, () -> packageService.updatePackage(1L, requestDto));
        verify(packageRepository, times(1)).findById(1L);
        verify(orderBookRepository, times(1)).findById(1L);
        verify(wrapTypeRepository, never()).findById(any());
        verify(packageRepository, never()).save(any());
    }

    @Test
    @DisplayName("포장을 업데이트 할때 포장지가 없으면 오류생김")
    void updatePackage_ShouldThrowException_WhenWrapTypeNotFound() {
        PackageRequestDto requestDto = PackageRequestDto.builder()
                .packageName("Updated Package")
                .orderBookId(1L)
                .packageTypeId(2L)
                .build();

        Packages existingPackage = Packages.builder()
                .packageId(1L)
                .packageType("Old Package")
                .build();

        OrderBook orderBook = new OrderBook();

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));
        when(orderBookRepository.findById(1L)).thenReturn(Optional.of(orderBook));
        when(wrapTypeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(WrapTypeNotFoundException.class, () -> packageService.updatePackage(1L, requestDto));
        verify(packageRepository, times(1)).findById(1L);
        verify(orderBookRepository, times(1)).findById(1L);
        verify(wrapTypeRepository, times(1)).findById(2L);
        verify(packageRepository, never()).save(any());
    }
}
