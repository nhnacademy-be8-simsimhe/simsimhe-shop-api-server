package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalResponseDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class OrderTotalServiceImplTest {

    @Mock
    private WrapTypeService wrapTypeService;
    @Mock
    private OrderListService orderListService;
    @Mock
    private DeliveryPolicyService deliveryPolicyService;
    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private OrderTotalServiceImpl orderTotalService;

    @Test
    void calculateTotal_Success() {
        // given
        Long userId = 1L;

        List<BookListResponseDto> bookOrderList = List.of(
                BookListResponseDto.builder().bookId(1L).price(BigDecimal.valueOf(10000)).quantity(2).build(),
                BookListResponseDto.builder().bookId(2L).price(BigDecimal.valueOf(20000)).quantity(1).build()
        );

        when(orderListService.toBookOrderList(anyList())).thenReturn(bookOrderList);

        when(wrapTypeService.getWrapTypeById(anyLong()))
                .thenReturn(WrapTypeResponseDto.builder().packagePrice(BigDecimal.valueOf(500)).build());

        doNothing().when(pointHistoryService).validateUsePoints(userId, BigDecimal.valueOf(5000));

        DeliveryPolicy mockPolicy = DeliveryPolicy.builder()
                .deliveryPrice(BigDecimal.valueOf(3000))
                .policyStandardPrice(BigDecimal.valueOf(50000))
                .build();

        when(deliveryPolicyService.getStandardPolicy()).thenReturn(mockPolicy);

        TotalRequestDto requestDto = new TotalRequestDto();
        requestDto.setUserId(userId);
        requestDto.setBookList(List.of(
                BookListRequestDto.builder().bookId(1L).quantity(2).build(),
                BookListRequestDto.builder().bookId(2L).quantity(1).build()
        ));
        requestDto.setPackagingOptions(Map.of(
                1L, createPackagingRequestDto(1L, 2)
        ));
        requestDto.setUsePoint(BigDecimal.valueOf(5000));

        TotalResponseDto responseDto = orderTotalService.calculateTotal(requestDto);

        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(39000), responseDto.getTotal()); // Expected total: 2x10000 + 20000 + 2x500 + 3000 - 5000
        assertEquals(BigDecimal.valueOf(3000), responseDto.getDeliveryPrice());
        verify(orderListService, times(1)).toBookOrderList(anyList());
        verify(pointHistoryService, times(1)).validateUsePoints(userId, BigDecimal.valueOf(5000));
        verify(deliveryPolicyService, times(1)).getStandardPolicy();
    }

    @Test
    void calculateDeliveryPrice_BelowStandard() {
        BigDecimal total = BigDecimal.valueOf(40000);

        DeliveryPolicy mockPolicy = DeliveryPolicy.builder()
                .deliveryPrice(BigDecimal.valueOf(3000))
                .policyStandardPrice(BigDecimal.valueOf(50000))
                .build();

        when(deliveryPolicyService.getStandardPolicy()).thenReturn(mockPolicy);

        BigDecimal deliveryPrice = orderTotalService.calculateDeliveryPrice(total);

        assertEquals(BigDecimal.valueOf(3000), deliveryPrice);
        verify(deliveryPolicyService, times(1)).getStandardPolicy();
    }

    @Test
    void calculateDeliveryPrice_AboveStandard() {
        BigDecimal total = BigDecimal.valueOf(60000);

        DeliveryPolicy mockPolicy = DeliveryPolicy.builder()
                .deliveryPrice(BigDecimal.valueOf(3000))
                .policyStandardPrice(BigDecimal.valueOf(50000))
                .build();

        when(deliveryPolicyService.getStandardPolicy()).thenReturn(mockPolicy);

        BigDecimal deliveryPrice = orderTotalService.calculateDeliveryPrice(total);

        assertEquals(BigDecimal.ZERO, deliveryPrice);
        verify(deliveryPolicyService, times(1)).getStandardPolicy();
    }

    @Test
    void wrapType_toEntity_Success() {
        // given
        WrapTypeRequestDto wrapTypeRequestDto = new WrapTypeRequestDto();
        wrapTypeRequestDto.setPackageName("Gift Wrap");
        wrapTypeRequestDto.setPackagePrice(BigDecimal.valueOf(1000));
        wrapTypeRequestDto.setIsAvailable(true);

        // when
        WrapType wrapType = wrapTypeRequestDto.toEntity();

        // then
        assertNotNull(wrapType);
        assertEquals("Gift Wrap", wrapType.getPackageName());
        assertEquals(BigDecimal.valueOf(1000), wrapType.getPackagePrice());
        assertTrue(wrapType.getIsAvailable());
    }

    private TotalRequestDto.PackagingRequestDto createPackagingRequestDto(Long packageTypeId, int quantity) {
        TotalRequestDto.PackagingRequestDto packagingRequestDto = new TotalRequestDto.PackagingRequestDto();
        packagingRequestDto.setPackageTypeId(packageTypeId);
        packagingRequestDto.setQuantity(quantity);
        return packagingRequestDto;
    }

    @Test
    void calculateTotal_DeliveryFeeExempted() {
        // given
        Long userId = 1L;

        // Mocked book order list (original price exceeds delivery fee exemption threshold)
        List<BookListResponseDto> bookOrderList = List.of(
                BookListResponseDto.builder().bookId(1L).price(BigDecimal.valueOf(30000)).quantity(1).build(),
                BookListResponseDto.builder().bookId(2L).price(BigDecimal.valueOf(25000)).quantity(1).build()
        );

        when(orderListService.toBookOrderList(anyList())).thenReturn(bookOrderList);

        // Mocked wrap type service
        when(wrapTypeService.getWrapTypeById(anyLong()))
                .thenReturn(WrapTypeResponseDto.builder().packagePrice(BigDecimal.valueOf(500)).build());

        // Mocked point validation
        doNothing().when(pointHistoryService).validateUsePoints(userId, BigDecimal.valueOf(5000));

        // Mocked delivery policy
        DeliveryPolicy mockPolicy = DeliveryPolicy.builder()
                .deliveryPrice(BigDecimal.valueOf(3000))
                .policyStandardPrice(BigDecimal.valueOf(50000)) //orders >= 50000
                .build();

        when(deliveryPolicyService.getStandardPolicy()).thenReturn(mockPolicy);

        // Request DTO with packaging options
        TotalRequestDto requestDto = new TotalRequestDto();
        requestDto.setUserId(userId);
        requestDto.setBookList(List.of(
                BookListRequestDto.builder().bookId(1L).quantity(1).build(),
                BookListRequestDto.builder().bookId(2L).quantity(1).build()
        ));
        requestDto.setPackagingOptions(Map.of(
                1L, createPackagingRequestDto(1L, 1)
        ));
        requestDto.setUsePoint(BigDecimal.valueOf(5000));

        // when
        TotalResponseDto responseDto = orderTotalService.calculateTotal(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(50500), responseDto.getTotal()); // 55000 + 500 (packaging) - 5000 (points)
        assertEquals(BigDecimal.ZERO, responseDto.getDeliveryPrice());
        assertEquals(BigDecimal.valueOf(55000), responseDto.getOriginalPrice());
        verify(orderListService, times(1)).toBookOrderList(anyList());
        verify(pointHistoryService, times(1)).validateUsePoints(userId, BigDecimal.valueOf(5000));
        verify(deliveryPolicyService, times(1)).getStandardPolicy();
    }

    @Test
    void calculateTotal_DeliveryFeeApplies() {
        Long userId = 1L;

        List<BookListResponseDto> bookOrderList = List.of(
                BookListResponseDto.builder().bookId(1L).price(BigDecimal.valueOf(10000)).quantity(2).build(),
                BookListResponseDto.builder().bookId(2L).price(BigDecimal.valueOf(20000)).quantity(1).build()
        );

        when(orderListService.toBookOrderList(anyList())).thenReturn(bookOrderList);

        // Mocked wrap type service
        when(wrapTypeService.getWrapTypeById(anyLong()))
                .thenReturn(WrapTypeResponseDto.builder().packagePrice(BigDecimal.valueOf(500)).build());

        // Mocked point validation
        doNothing().when(pointHistoryService).validateUsePoints(userId, BigDecimal.valueOf(5000));

        // Mocked delivery policy
        DeliveryPolicy mockPolicy = DeliveryPolicy.builder()
                .deliveryPrice(BigDecimal.valueOf(3000))
                .policyStandardPrice(BigDecimal.valueOf(50000)) // orders >= 50000
                .build();

        when(deliveryPolicyService.getStandardPolicy()).thenReturn(mockPolicy);

        TotalRequestDto requestDto = new TotalRequestDto();
        requestDto.setUserId(userId);
        requestDto.setBookList(List.of(
                BookListRequestDto.builder().bookId(1L).quantity(2).build(),
                BookListRequestDto.builder().bookId(2L).quantity(1).build()
        ));
        requestDto.setPackagingOptions(Map.of(
                1L, createPackagingRequestDto(1L, 2)
        ));
        requestDto.setUsePoint(BigDecimal.valueOf(5000));

        TotalResponseDto responseDto = orderTotalService.calculateTotal(requestDto);

        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(39000), responseDto.getTotal()); // 40000 + 1000 (packaging) + 3000 (delivery) - 5000 (points)
        assertEquals(BigDecimal.valueOf(3000), responseDto.getDeliveryPrice());
        assertEquals(BigDecimal.valueOf(40000), responseDto.getOriginalPrice());
        verify(orderListService, times(1)).toBookOrderList(anyList());
        verify(pointHistoryService, times(1)).validateUsePoints(userId, BigDecimal.valueOf(5000));
        verify(deliveryPolicyService, times(1)).getStandardPolicy();
    }

    @Test
    void calculateTotal_NoPackaging() {
        // given
        Long userId = 1L;

        // Mocked book order list
        List<BookListResponseDto> bookOrderList = List.of(
                BookListResponseDto.builder().bookId(1L).price(BigDecimal.valueOf(15000)).quantity(2).build(),
                BookListResponseDto.builder().bookId(2L).price(BigDecimal.valueOf(20000)).quantity(1).build()
        );

        when(orderListService.toBookOrderList(anyList())).thenReturn(bookOrderList);

        // Mocked point validation
        doNothing().when(pointHistoryService).validateUsePoints(userId, BigDecimal.valueOf(5000));

        // Mocked delivery policy
        DeliveryPolicy mockPolicy = DeliveryPolicy.builder()
                .deliveryPrice(BigDecimal.valueOf(3000))
                .policyStandardPrice(BigDecimal.valueOf(50000))
                .build();

        when(deliveryPolicyService.getStandardPolicy()).thenReturn(mockPolicy);

        // Request DTO without packaging options
        TotalRequestDto requestDto = new TotalRequestDto();
        requestDto.setUserId(userId);
        requestDto.setBookList(List.of(
                BookListRequestDto.builder().bookId(1L).quantity(2).build(),
                BookListRequestDto.builder().bookId(2L).quantity(1).build()
        ));
        requestDto.setPackagingOptions(null); // No packaging
        requestDto.setUsePoint(BigDecimal.valueOf(5000));

        // when
        TotalResponseDto responseDto = orderTotalService.calculateTotal(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(50000), responseDto.getOriginalPrice()); // 2x15000 + 20000
        assertEquals(BigDecimal.valueOf(45000), responseDto.getTotal()); // 55000 - 5000 (points)
        assertEquals(BigDecimal.valueOf(0), responseDto.getDeliveryPrice());
        verify(orderListService, times(1)).toBookOrderList(anyList());
        verify(pointHistoryService, times(1)).validateUsePoints(userId, BigDecimal.valueOf(5000));
        verify(deliveryPolicyService, times(1)).getStandardPolicy();
    }
}

