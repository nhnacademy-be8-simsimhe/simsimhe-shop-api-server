package com.simsimbookstore.apiserver.coupons.coupon.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.coupons.coupon.dto.*;
import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()) // Jackson 메시지 컨버터 추가
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * 테스트 메서드: GET /api/coupons/{couponId} - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("GET /api/shop/coupons/{couponId} - FixCouponResponseDto 반환 시 성공")
    void getCoupon_FixCoupon_Success() throws Exception {
        Long couponId = 1001L;
        FixCouponResponseDto couponResponseDto = FixCouponResponseDto.builder()
                .couponId(couponId)
                .issueDate(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(30))
                .couponStatus(CouponStatus.UNUSED)
                .couponTypeName("Fix Discount")
                .isStacking(false)
                .couponTargetType(CouponTargetType.ALL)
                .couponTargetId(null)
                .discountPrice(new BigDecimal("10.00"))
                .minOrderAmount(new BigDecimal("50.00"))
                .build();

        when(couponService.getCouponById(couponId)).thenReturn(couponResponseDto);

        mockMvc.perform(get("/api/shop/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(couponId.intValue())))
                .andExpect(jsonPath("$.couponTypeName", is("Fix Discount")))
                .andExpect(jsonPath("$.discountPrice", is(10.00)))
                .andExpect(jsonPath("$.disCountType", is("정액")))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("미사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Fix Discount")))
                .andExpect(jsonPath("$.stacking", is(false)))
                .andExpect(jsonPath("$.couponTargetType", is("전체")))
                .andExpect(jsonPath("$.couponTargetId", is(nullValue())))
                .andExpect(jsonPath("$.minOrderAmount", is(50.00)));
    }

    /**
     * 테스트 메서드: GET /api/coupons/{couponId} - RateCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("GET /api/shop/coupons/{couponId} - RateCouponResponseDto 반환 시 성공")
    void getCoupon_RateCoupon_Success() throws Exception {
        Long couponId = 1002L;
        RateCouponResponseDto couponResponseDto = RateCouponResponseDto.builder()
                .couponId(couponId)
                .issueDate(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(30))
                .couponStatus(CouponStatus.UNUSED)
                .couponTypeName("Rate Discount")
                .isStacking(true)
                .couponTargetType(CouponTargetType.CATEGORY)
                .couponTargetId(200L)
                .discountRate(new BigDecimal("15.00"))
                .maxDiscountAmount(new BigDecimal("30.00"))
                .minOrderAmount(new BigDecimal("100.00"))
                .build();

        when(couponService.getCouponById(couponId)).thenReturn(couponResponseDto);

        mockMvc.perform(get("/api/shop/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(couponId.intValue())))
                .andExpect(jsonPath("$.couponTypeName", is("Rate Discount")))
                .andExpect(jsonPath("$.discountRate", is(15.00)))
                .andExpect(jsonPath("$.minOrderAmount", is(100.00)))
                .andExpect(jsonPath("$.maxDiscountAmount", is(30.00)))
                .andExpect(jsonPath("$.disCountType", is("정률")))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("미사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Rate Discount")))
                .andExpect(jsonPath("$.stacking", is(true)))
                .andExpect(jsonPath("$.couponTargetType", is("카테고리")))
                .andExpect(jsonPath("$.couponTargetId", is(200)));
    }

    /**
     * 테스트 메서드: GET /api/coupons/{couponId} - 쿠폰 없음
     */
    @Test
    @DisplayName("GET /api/shop/coupons/{couponId} - 쿠폰 없음")
    @Disabled
    void getCoupon_NotFound() throws Exception {
        Long couponId = 9999L;

        when(couponService.getCouponById(couponId))
                .thenThrow(new NotFoundException("쿠폰(id:" + couponId + ")이 존재하지 않습니다."));

        mockMvc.perform(get("/api/shop/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 메서드: GET /api/users/{userId}/coupons/unused?couponTypeId=xxx - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("GET /api/shop/users/{userId}/coupons/unused?couponTypeId=300 - FixCouponResponseDto 반환 시 성공")
    void getUnusedCouponByCouponType_FixCoupon_Success() throws Exception {
        Long userId = 1L;
        Long couponTypeId = 300L;
        FixCouponResponseDto couponResponseDto = FixCouponResponseDto.builder()
                .couponId(1003L)
                .issueDate(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(15))
                .couponStatus(CouponStatus.UNUSED)
                .couponTypeName("Fix Discount Type 300")
                .isStacking(false)
                .couponTargetType(CouponTargetType.ALL)
                .couponTargetId(null)
                .discountPrice(new BigDecimal("20.00"))
                .minOrderAmount(new BigDecimal("80.00"))
                .build();

        when(couponService.getUnusedCouponByCouponType(userId, couponTypeId)).thenReturn(couponResponseDto);

        mockMvc.perform(get("/api/shop/users/{userId}/coupons/unused", userId)
                        .param("couponTypeId", String.valueOf(couponTypeId))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(1003)))
                .andExpect(jsonPath("$.couponTypeName", is("Fix Discount Type 300")))
                // description 필드가 DTO에 없으므로 제거하거나 관련 필드로 대체
                // .andExpect(jsonPath("$.description", is("")))
                .andExpect(jsonPath("$.discountPrice", is(20.00)))
                .andExpect(jsonPath("$.disCountType", is("정액")))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("미사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Fix Discount Type 300")))
                .andExpect(jsonPath("$.stacking", is(false)))
                .andExpect(jsonPath("$.couponTargetType", is("전체")))
                .andExpect(jsonPath("$.couponTargetId", is(nullValue())))
                .andExpect(jsonPath("$.minOrderAmount", is(80.00)));
    }

    /**
     * 테스트 메서드: GET /api/users/{userId}/coupons/unused?couponTypeId=xxx - RateCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("GET /api/shop/users/{userId}/coupons/unused?couponTypeId=400 - RateCouponResponseDto 반환 시 성공")
    void getUnusedCouponByCouponType_RateCoupon_Success() throws Exception {
        Long userId = 2L;
        Long couponTypeId = 400L;
        RateCouponResponseDto couponResponseDto = RateCouponResponseDto.builder()
                .couponId(1004L)
                .issueDate(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(20))
                .couponStatus(CouponStatus.UNUSED)
                .couponTypeName("Rate Discount Type 400")
                .isStacking(true)
                .couponTargetType(CouponTargetType.BOOK)
                .couponTargetId(300L)
                .discountRate(new BigDecimal("10.00"))
                .maxDiscountAmount(new BigDecimal("25.00"))
                .minOrderAmount(new BigDecimal("120.00"))
                .build();

        when(couponService.getUnusedCouponByCouponType(userId, couponTypeId)).thenReturn(couponResponseDto);

        mockMvc.perform(get("/api/shop/users/{userId}/coupons/unused", userId)
                        .param("couponTypeId", String.valueOf(couponTypeId))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(1004)))
                .andExpect(jsonPath("$.couponTypeName", is("Rate Discount Type 400")))
                .andExpect(jsonPath("$.discountRate", is(10.00)))
                .andExpect(jsonPath("$.minOrderAmount", is(120.00)))
                .andExpect(jsonPath("$.maxDiscountAmount", is(25.00)))
                .andExpect(jsonPath("$.disCountType", is("정률")))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("미사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Rate Discount Type 400")))
                .andExpect(jsonPath("$.stacking", is(true)))
                .andExpect(jsonPath("$.couponTargetType", is("책")))
                .andExpect(jsonPath("$.couponTargetId", is(300)));
    }

    /**
     * 테스트 메서드: GET /api/users/{userId}/coupons/unused?bookId=xxx - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("GET /api/shop/users/{userId}/coupons/unused?bookId=200 - FixCouponResponseDto 반환 시 성공")
    void getEligibleCouponsToBook_FixCoupon_Success() throws Exception {
        Long userId = 1L;
        Long bookId = 200L;
        String sortField = "issueDate";

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, sortField));
        List<CouponResponseDto> couponList = Arrays.asList(
                FixCouponResponseDto.builder()
                        .couponId(1005L)
                        .issueDate(LocalDateTime.now())
                        .deadline(LocalDateTime.now().plusDays(10))
                        .couponStatus(CouponStatus.UNUSED)
                        .couponTypeName("Fix Discount for Book")
                        .isStacking(false)
                        .couponTargetType(CouponTargetType.BOOK)
                        .couponTargetId(bookId)
                        .discountPrice(new BigDecimal("5.00"))
                        .minOrderAmount(new BigDecimal("40.00"))
                        .build(),
                FixCouponResponseDto.builder()
                        .couponId(1006L)
                        .issueDate(LocalDateTime.now())
                        .deadline(LocalDateTime.now().plusDays(15))
                        .couponStatus(CouponStatus.UNUSED)
                        .couponTypeName("Another Fix Discount for Book")
                        .isStacking(true)
                        .couponTargetType(CouponTargetType.BOOK)
                        .couponTargetId(bookId)
                        .discountPrice(new BigDecimal("10.00"))
                        .minOrderAmount(new BigDecimal("60.00"))
                        .build()
        );
        Page<CouponResponseDto> couponPage = new PageImpl<>(couponList, pageable, couponList.size());

        when(couponService.getEligibleCoupons(Mockito.any(Pageable.class), eq(userId), eq(bookId))).thenReturn(couponPage);

        mockMvc.perform(get("/api/shop/users/{userId}/coupons/unused", userId)
                        .param("bookId", String.valueOf(bookId))
                        .param("sortField", sortField)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].couponId", is(1005)))
                .andExpect(jsonPath("$.content[0].couponTypeName", is("Fix Discount for Book")))
                .andExpect(jsonPath("$.content[0].discountPrice", is(5.00)))
                .andExpect(jsonPath("$.content[0].disCountType", is("정액")))
                .andExpect(jsonPath("$.content[0].issueDate", notNullValue()))
                .andExpect(jsonPath("$.content[0].deadline", notNullValue()))
                .andExpect(jsonPath("$.content[0].couponStatus", is("미사용")))
                .andExpect(jsonPath("$.content[0].couponTypeName", is("Fix Discount for Book")))
                .andExpect(jsonPath("$.content[0].stacking", is(false)))
                .andExpect(jsonPath("$.content[0].couponTargetType", is("책")))
                .andExpect(jsonPath("$.content[0].couponTargetId", is(200)))
                .andExpect(jsonPath("$.content[0].minOrderAmount", is(40.00)))
                .andExpect(jsonPath("$.content[0].discountPrice", is(5.00)))
                .andExpect(jsonPath("$.content[1].couponId", is(1006)))
                .andExpect(jsonPath("$.content[1].couponTypeName", is("Another Fix Discount for Book")))
                .andExpect(jsonPath("$.content[1].discountPrice", is(10.00)))
                .andExpect(jsonPath("$.content[1].disCountType", is("정액")))
                .andExpect(jsonPath("$.content[1].issueDate", notNullValue()))
                .andExpect(jsonPath("$.content[1].deadline", notNullValue()))
                .andExpect(jsonPath("$.content[1].couponStatus", is("미사용")))
                .andExpect(jsonPath("$.content[1].couponTypeName", is("Another Fix Discount for Book")))
                .andExpect(jsonPath("$.content[1].stacking", is(true)))
                .andExpect(jsonPath("$.content[1].couponTargetType", is("책")))
                .andExpect(jsonPath("$.content[1].couponTargetId", is(200)))
                .andExpect(jsonPath("$.content[1].minOrderAmount", is(60.00)))
                .andExpect(jsonPath("$.content[1].discountPrice", is(10.00)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    /**
     * 테스트 메서드: GET /api/users/{userId}/coupons/unused - 유저의 미사용 쿠폰 조회 - Fix 및 Rate 쿠폰 혼합 반환 시 성공
     */
    @Test
    @DisplayName("GET /api/shop/users/{userId}/coupons/unused - Fix 및 Rate 쿠폰 혼합 반환 시 성공")
    void getUnusedCoupons_MixedCoupons_Success() throws Exception {
        Long userId = 1L;
        String sortField = "issueDate";

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, sortField));
        List<CouponResponseDto> couponList = Arrays.asList(
                FixCouponResponseDto.builder()
                        .couponId(1007L)
                        .issueDate(LocalDateTime.now())
                        .deadline(LocalDateTime.now().plusDays(25))
                        .couponStatus(CouponStatus.UNUSED)
                        .couponTypeName("Fix Discount Mixed")
                        .isStacking(false)
                        .couponTargetType(CouponTargetType.ALL)
                        .couponTargetId(null)
                        .discountPrice(new BigDecimal("15.00"))
                        .minOrderAmount(new BigDecimal("70.00"))
                        .build(),
                RateCouponResponseDto.builder()
                        .couponId(1008L)
                        .issueDate(LocalDateTime.now())
                        .deadline(LocalDateTime.now().plusDays(30))
                        .couponStatus(CouponStatus.UNUSED)
                        .couponTypeName("Rate Discount Mixed")
                        .isStacking(true)
                        .couponTargetType(CouponTargetType.CATEGORY)
                        .couponTargetId(400L)
                        .discountRate(new BigDecimal("20.00"))
                        .maxDiscountAmount(new BigDecimal("50.00"))
                        .minOrderAmount(new BigDecimal("150.00"))
                        .build()
        );
        Page<CouponResponseDto> couponPage = new PageImpl<>(couponList, pageable, couponList.size());

        when(couponService.getUnusedCoupons(Mockito.any(Pageable.class), eq(userId))).thenReturn(couponPage);

        mockMvc.perform(get("/api/shop/users/{userId}/coupons/unused", userId)
                        .param("sortField", sortField)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                // FixCouponResponseDto 검증
                .andExpect(jsonPath("$.content[0].couponId", is(1007)))
                .andExpect(jsonPath("$.content[0].couponTypeName", is("Fix Discount Mixed")))
                .andExpect(jsonPath("$.content[0].discountPrice", is(15.00)))
                .andExpect(jsonPath("$.content[0].disCountType", is("정액")))
                .andExpect(jsonPath("$.content[0].issueDate", notNullValue()))
                .andExpect(jsonPath("$.content[0].deadline", notNullValue()))
                .andExpect(jsonPath("$.content[0].couponStatus", is("미사용")))
                .andExpect(jsonPath("$.content[0].couponTypeName", is("Fix Discount Mixed")))
                .andExpect(jsonPath("$.content[0].stacking", is(false)))
                .andExpect(jsonPath("$.content[0].couponTargetType", is("전체")))
                .andExpect(jsonPath("$.content[0].couponTargetId", is(nullValue())))
                .andExpect(jsonPath("$.content[0].minOrderAmount", is(70.00)))
                .andExpect(jsonPath("$.content[0].discountPrice", is(15.00)))
                // RateCouponResponseDto 검증
                .andExpect(jsonPath("$.content[1].couponId", is(1008)))
                .andExpect(jsonPath("$.content[1].couponTypeName", is("Rate Discount Mixed")))
                .andExpect(jsonPath("$.content[1].discountRate", is(20.00)))
                .andExpect(jsonPath("$.content[1].minOrderAmount", is(150.00)))
                .andExpect(jsonPath("$.content[1].maxDiscountAmount", is(50.00)))
                .andExpect(jsonPath("$.content[1].disCountType", is("정률")))
                .andExpect(jsonPath("$.content[1].issueDate", notNullValue()))
                .andExpect(jsonPath("$.content[1].deadline", notNullValue()))
                .andExpect(jsonPath("$.content[1].couponStatus", is("미사용")))
                .andExpect(jsonPath("$.content[1].couponTypeName", is("Rate Discount Mixed")))
                .andExpect(jsonPath("$.content[1].stacking", is(true)))
                .andExpect(jsonPath("$.content[1].couponTargetType", is("카테고리")))
                .andExpect(jsonPath("$.content[1].couponTargetId", is(400)))
                .andExpect(jsonPath("$.content[1].discountRate", is(20.00)))
                .andExpect(jsonPath("$.content[1].minOrderAmount", is(150.00)))
                .andExpect(jsonPath("$.content[1].maxDiscountAmount", is(50.00)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    /**
     * 테스트 메서드: POST /api/coupons/issue - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("POST /api/admin/coupons/issue - FixCouponResponseDto 반환 시 성공")
    void issueCoupons_FixCoupon_Success() throws Exception {
        IssueCouponsRequestDto requestDto = IssueCouponsRequestDto.builder()
                .userIds(Arrays.asList(1L, 2L))
                .couponTypeId(100L)
                .build();



//        List<CouponResponseDto> responseDtoList = Arrays.asList(
//                FixCouponResponseDto.builder()
//                        .couponId(1009L)
//                        .issueDate(LocalDateTime.now())
//                        .deadline(LocalDateTime.now().plusDays(30))
//                        .couponStatus(CouponStatus.UNUSED)
//                        .couponTypeName("Issued Fix Discount")
//                        .isStacking(false)
//                        .couponTargetType(CouponTargetType.ALL)
//                        .couponTargetId(null)
//                        .discountPrice(new BigDecimal("25.00"))
//                        .minOrderAmount(new BigDecimal("100.00"))
//                        .build(),
//                FixCouponResponseDto.builder()
//                        .couponId(1010L)
//                        .issueDate(LocalDateTime.now())
//                        .deadline(LocalDateTime.now().plusDays(30))
//                        .couponStatus(CouponStatus.UNUSED)
//                        .couponTypeName("Issued Fix Discount")
//                        .isStacking(false)
//                        .couponTargetType(CouponTargetType.ALL)
//                        .couponTargetId(null)
//                        .discountPrice(new BigDecimal("30.00"))
//                        .minOrderAmount(new BigDecimal("120.00"))
//                        .build()
//        );
        List<Long> couponIds = List.of(1009L, 1010L);

        when(couponService.issueCoupons(anyList(), eq(100L))).thenReturn(couponIds);

        mockMvc.perform(post("/api/admin/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    /**
     * 테스트 메서드: POST /api/coupons/issue - RateCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("POST /api/admin/coupons/issue - RateCouponResponseDto 반환 시 성공")
    void issueCoupons_RateCoupon_Success() throws Exception {
        IssueCouponsRequestDto requestDto = IssueCouponsRequestDto.builder()
                .userIds(Arrays.asList(3L, 4L))
                .couponTypeId(200L)
                .build();

//        List<CouponResponseDto> responseDtoList = Arrays.asList(
//                RateCouponResponseDto.builder()
//                        .couponId(1011L)
//                        .issueDate(LocalDateTime.now())
//                        .deadline(LocalDateTime.now().plusDays(30))
//                        .couponStatus(CouponStatus.UNUSED)
//                        .couponTypeName("Issued Rate Discount")
//                        .isStacking(true)
//                        .couponTargetType(CouponTargetType.CATEGORY)
//                        .couponTargetId(500L)
//                        .discountRate(new BigDecimal("20.00"))
//                        .maxDiscountAmount(new BigDecimal("40.00"))
//                        .minOrderAmount(new BigDecimal("200.00"))
//                        .build(),
//                RateCouponResponseDto.builder()
//                        .couponId(1012L)
//                        .issueDate(LocalDateTime.now())
//                        .deadline(LocalDateTime.now().plusDays(30))
//                        .couponStatus(CouponStatus.UNUSED)
//                        .couponTypeName("Issued Rate Discount")
//                        .isStacking(true)
//                        .couponTargetType(CouponTargetType.BOOK)
//                        .couponTargetId(600L)
//                        .discountRate(new BigDecimal("15.00"))
//                        .maxDiscountAmount(new BigDecimal("30.00"))
//                        .minOrderAmount(new BigDecimal("180.00"))
//                        .build()
//        );
        List<Long> couponIds = List.of(1009L, 1010L);


        when(couponService.issueCoupons(anyList(), eq(200L))).thenReturn(couponIds);

        mockMvc.perform(post("/api/admin/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 테스트 메서드: POST /api/coupons/issue - 사용자 중 하나가 존재하지 않아 NotFoundException 발생
     */
    @Test
    @DisplayName("POST /api/admin/coupons/issue - 사용자 없음")
    @Disabled
    void issueCoupons_UserNotFound() throws Exception {
        IssueCouponsRequestDto requestDto = IssueCouponsRequestDto.builder()
                .userIds(Arrays.asList(1L, 5L))
                .couponTypeId(100L)
                .build();

        when(couponService.issueCoupons(anyList(), eq(100L)))
                .thenThrow(new NotFoundException("회원(id:5)이 존재하지 않습니다."));

        mockMvc.perform(post("/api/admin/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 메서드: POST /api/users/{userId}/coupons/{couponId}/expired - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("POST /api/admin/users/{userId}/coupons/{couponId}/expired - FixCouponResponseDto 반환 시 성공")
    void expiredCoupon_FixCoupon_Success() throws Exception {
        Long userId = 1L;
        Long couponId = 1013L;
        FixCouponResponseDto responseDto = FixCouponResponseDto.builder()
                .couponId(couponId)
                .issueDate(LocalDateTime.now().minusDays(10))
                .deadline(LocalDateTime.now().plusDays(20))
                .couponStatus(CouponStatus.EXPIRED)
                .couponTypeName("Expired Fix Discount")
                .isStacking(false)
                .couponTargetType(CouponTargetType.ALL)
                .couponTargetId(null)
                .discountPrice(new BigDecimal("15.00"))
                .minOrderAmount(new BigDecimal("90.00"))
                .build();

        when(couponService.expireCoupon(userId, couponId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/users/{userId}/coupons/{couponId}/expired", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(couponId.intValue())))
                .andExpect(jsonPath("$.couponTypeName", is("Expired Fix Discount")))
                .andExpect(jsonPath("$.discountPrice", is(15.00)))
                .andExpect(jsonPath("$.disCountType", is("정액")))
                .andExpect(jsonPath("$.minOrderAmount", is(90.00)))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("만료")))
                .andExpect(jsonPath("$.couponTypeName", is("Expired Fix Discount")))
                .andExpect(jsonPath("$.stacking", is(false)))
                .andExpect(jsonPath("$.couponTargetType", is("전체")))
                .andExpect(jsonPath("$.couponTargetId", is(nullValue())))
                .andExpect(jsonPath("$.discountPrice", is(15.00)));
    }

    /**
     * 테스트 메서드: POST /api/users/{userId}/coupons/{couponId}/expired - RateCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("POST /api/admin/users/{userId}/coupons/{couponId}/expired - RateCouponResponseDto 반환 시 성공")
    void expiredCoupon_RateCoupon_Success() throws Exception {
        Long userId = 2L;
        Long couponId = 1014L;
        RateCouponResponseDto responseDto = RateCouponResponseDto.builder()
                .couponId(couponId)
                .issueDate(LocalDateTime.now().minusDays(15))
                .deadline(LocalDateTime.now().plusDays(15))
                .couponStatus(CouponStatus.USED)
                .couponTypeName("Used Rate Discount")
                .isStacking(true)
                .couponTargetType(CouponTargetType.CATEGORY)
                .couponTargetId(700L)
                .discountRate(new BigDecimal("25.00"))
                .maxDiscountAmount(new BigDecimal("60.00"))
                .minOrderAmount(new BigDecimal("200.00"))
                .build();

        when(couponService.expireCoupon(userId, couponId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/users/{userId}/coupons/{couponId}/expired", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(couponId.intValue())))
                .andExpect(jsonPath("$.couponTypeName", is("Used Rate Discount")))
                .andExpect(jsonPath("$.discountRate", is(25.00)))
                .andExpect(jsonPath("$.minOrderAmount", is(200.00)))
                .andExpect(jsonPath("$.maxDiscountAmount", is(60.00)))
                .andExpect(jsonPath("$.disCountType", is("정률")))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Used Rate Discount")))
                .andExpect(jsonPath("$.stacking", is(true)))
                .andExpect(jsonPath("$.couponTargetType", is("카테고리")))
                .andExpect(jsonPath("$.couponTargetId", is(700)));
    }

    /**
     * 테스트 메서드: POST /api/users/{userId}/coupons/{couponId}/expired - 쿠폰 없음
     */
    @Test
    @DisplayName("POST /api/admin/users/{userId}/coupons/{couponId}/expired - 쿠폰 없음")
    @Disabled
    void expiredCoupon_NotFound() throws Exception {
        Long userId = 1L;
        Long couponId = 9999L;

        when(couponService.expireCoupon(userId, couponId))
                .thenThrow(new NotFoundException("쿠폰(id:" + couponId + ")이 존재하지 않습니다."));

        mockMvc.perform(post("/api/admin/users/{userId}/coupons/{couponId}/expired", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 메서드: POST /api/users/{userId}/coupons/{couponId}/use - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("POST /api/shop/users/{userId}/coupons/{couponId}/use - FixCouponResponseDto 반환 시 성공")
    void useCoupon_FixCoupon_Success() throws Exception {
        Long userId = 1L;
        Long couponId = 1015L;
        FixCouponResponseDto responseDto = FixCouponResponseDto.builder()
                .couponId(couponId)
                .issueDate(LocalDateTime.now().minusDays(5))
                .deadline(LocalDateTime.now().plusDays(25))
                .couponStatus(CouponStatus.USED)
                .couponTypeName("Used Fix Discount")
                .isStacking(false)
                .couponTargetType(CouponTargetType.ALL)
                .couponTargetId(null)
                .discountPrice(new BigDecimal("20.00"))
                .minOrderAmount(new BigDecimal("100.00"))
                .build();

        when(couponService.expireCoupon(userId, couponId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/shop/users/{userId}/coupons/{couponId}/use", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(couponId.intValue())))
                .andExpect(jsonPath("$.couponTypeName", is("Used Fix Discount")))
                .andExpect(jsonPath("$.discountPrice", is(20.00)))
                .andExpect(jsonPath("$.disCountType", is("정액")))
                .andExpect(jsonPath("$.minOrderAmount", is(100.00)))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Used Fix Discount")))
                .andExpect(jsonPath("$.stacking", is(false)))
                .andExpect(jsonPath("$.couponTargetType", is("전체")))
                .andExpect(jsonPath("$.couponTargetId", is(nullValue())))
                .andExpect(jsonPath("$.discountPrice", is(20.00)));
    }

    /**
     * 테스트 메서드: POST /api/users/{userId}/coupons/{couponId}/use - RateCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("POST /api/shop/users/{userId}/coupons/{couponId}/use - RateCouponResponseDto 반환 시 성공")
    void useCoupon_RateCoupon_Success() throws Exception {
        Long userId = 2L;
        Long couponId = 1016L;
        RateCouponResponseDto responseDto = RateCouponResponseDto.builder()
                .couponId(couponId)
                .issueDate(LocalDateTime.now().minusDays(10))
                .deadline(LocalDateTime.now().plusDays(20))
                .couponStatus(CouponStatus.USED)
                .couponTypeName("Used Rate Discount")
                .isStacking(true)
                .couponTargetType(CouponTargetType.BOOK)
                .couponTargetId(800L)
                .discountRate(new BigDecimal("30.00"))
                .maxDiscountAmount(new BigDecimal("70.00"))
                .minOrderAmount(new BigDecimal("250.00"))
                .build();

        when(couponService.expireCoupon(userId, couponId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/shop/users/{userId}/coupons/{couponId}/use", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponId", is(couponId.intValue())))
                .andExpect(jsonPath("$.couponTypeName", is("Used Rate Discount")))
                .andExpect(jsonPath("$.discountRate", is(30.00)))
                .andExpect(jsonPath("$.minOrderAmount", is(250.00)))
                .andExpect(jsonPath("$.maxDiscountAmount", is(70.00)))
                .andExpect(jsonPath("$.disCountType", is("정률")))
                .andExpect(jsonPath("$.issueDate", notNullValue()))
                .andExpect(jsonPath("$.deadline", notNullValue()))
                .andExpect(jsonPath("$.couponStatus", is("사용")))
                .andExpect(jsonPath("$.couponTypeName", is("Used Rate Discount")))
                .andExpect(jsonPath("$.stacking", is(true)))
                .andExpect(jsonPath("$.couponTargetType", is("책")))
                .andExpect(jsonPath("$.couponTargetId", is(800)));
    }

    /**
     * 테스트 메서드: POST /api/users/{userId}/coupons/{couponId}/use - 쿠폰 없음
     */
    @Test
    @DisplayName("POST /api/shop/users/{userId}/coupons/{couponId}/use - 쿠폰 없음")
    @Disabled
    void useCoupon_NotFound() throws Exception {
        Long userId = 1L;
        Long couponId = 9999L;

        when(couponService.expireCoupon(userId, couponId))
                .thenThrow(new NotFoundException("쿠폰(id:" + couponId + ")이 존재하지 않습니다."));

        mockMvc.perform(post("/api/users/{userId}/coupons/{couponId}/use", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 메서드: DELETE /api/users/{userId}/coupons/{couponId} - FixCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("DELETE /api/admin/users/{userId}/coupons/{couponId} - FixCouponResponseDto 반환 시 성공")
    void deleteCoupon_FixCoupon_Success() throws Exception {
        Long userId = 1L;
        Long couponId = 1017L;

        doNothing().when(couponService).deleteCoupon(userId, couponId);

        mockMvc.perform(delete("/api/admin/users/{userId}/coupons/{couponId}", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk());
    }

    /**
     * 테스트 메서드: DELETE /api/users/{userId}/coupons/{couponId} - RateCouponResponseDto 반환 시 성공
     */
    @Test
    @DisplayName("DELETE /api/admin/users/{userId}/coupons/{couponId} - RateCouponResponseDto 반환 시 성공")
    void deleteCoupon_RateCoupon_Success() throws Exception {
        Long userId = 2L;
        Long couponId = 1018L;

        doNothing().when(couponService).deleteCoupon(userId, couponId);

        mockMvc.perform(delete("/api/admin/users/{userId}/coupons/{couponId}", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk());
    }

    /**
     * 테스트 메서드: DELETE /api/users/{userId}/coupons/{couponId} - 쿠폰 없음
     */
    @Test
    @DisplayName("DELETE /api/admin/users/{userId}/coupons/{couponId} - 쿠폰 없음")
    @Disabled
    void deleteCoupon_NotFound() throws Exception {
        Long userId = 1L;
        Long couponId = 9999L;

        doThrow(new NotFoundException("쿠폰(id:" + couponId + ")이 존재하지 않습니다."))
                .when(couponService).deleteCoupon(userId, couponId);

        mockMvc.perform(delete("/api/admin/users/{userId}/coupons/{couponId}", userId, couponId)
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 메서드: GET /api/coupons/{couponId}/calculate - 성공
     */
    @Test
    @DisplayName("GET /api/shop/coupons/{couponId}/calculate - 성공")
    void calDiscountAmount_Success() throws Exception {
        Long couponId = 1019L;
        Long bookId = 300L;
        Integer quantity = 3;

        DiscountAmountResponseDto discountDto = DiscountAmountResponseDto.builder()
                .discountAmount(new BigDecimal("150.00"))
                .build();

        when(couponService.calDiscountAmount(bookId, quantity, couponId)).thenReturn(discountDto);

        mockMvc.perform(get("/api/shop/coupons/{couponId}/calculate", couponId)
                        .param("bookId", String.valueOf(bookId))
                        .param("quantity", String.valueOf(quantity))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.discountAmount", is(150.00)));
    }

    /**
     * 테스트 메서드: GET /api/coupons/{couponId}/calculate - 쿠폰 없음
     */
    @Test
    @DisplayName("GET /api/shop/coupons/{couponId}/calculate - 쿠폰 없음")
    @Disabled
    void calDiscountAmount_NotFound() throws Exception {
        Long couponId = 9999L;
        Long bookId = 300L;
        Integer quantity = 3;

        when(couponService.calDiscountAmount(bookId, quantity, couponId))
                .thenThrow(new NotFoundException("쿠폰(id:" + couponId + ")이 존재하지 않습니다."));

        mockMvc.perform(get("/api/shop/coupons/{couponId}/calculate", couponId)
                        .param("bookId", String.valueOf(bookId))
                        .param("quantity", String.valueOf(quantity))
                        .accept(MediaType.APPLICATION_JSON)) // Accept 헤더 설정
                .andExpect(status().isNotFound());
    }
}