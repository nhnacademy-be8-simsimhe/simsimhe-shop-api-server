package com.simsimbookstore.apiserver.coupons.coupon.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.IssueCouponsRequestDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;
}