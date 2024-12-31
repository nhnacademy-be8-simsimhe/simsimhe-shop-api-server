package com.simsimbookstore.apiserver.coupons.coupontype.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.exception.AlreadyCouponTypeIssue;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeRequestDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.mapper.CouponTypeMapper;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponTypeServiceImplTest {
    @Mock
    private CouponTypeRepository couponTypeRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CouponTypeServiceImpl couponTypeService;


    private CouponType couponType1;
    private CouponType couponType2;
    private CouponType couponType3;

    @BeforeEach
    void setup() {
        CouponPolicy couponPolicy1 = CouponPolicy.builder().couponPolicyId(1L).build();
        CouponPolicy couponPolicy2 = CouponPolicy.builder().couponPolicyId(2L).build();
        Book book = Book.builder().bookId(100L).title("Test Book").build();
        Category category = Category.builder().categoryId(1001L).categoryName("Test Category").build();


        couponType1 = BookCoupon.builder()
                 .couponTypeId(1L)
                 .couponTypeName("Book Discount")
                 .couponPolicy(couponPolicy1)
                 .book(book)
                 .build();

        couponType2 = CategoryCoupon.builder()
                .couponTypeId(2L)
                .couponTypeName("Category Discount")
                .couponPolicy(couponPolicy1)
                .category(category)
                .build();

        couponType3 = AllCoupon.builder()
                .couponTypeId(3L)
                .couponTypeName("All Discount")
                .couponPolicy(couponPolicy2)
                .build();
    }

    @Test
    @DisplayName("getAllCouponType : 정상 동작")
    void getAllCouponType() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("couponTypeId").ascending());

        Page<CouponType> couponTypePage = new PageImpl<>(Arrays.asList(couponType1, couponType2,couponType3), pageable, 3);

        when(couponTypeRepository.findAll(pageable)).thenReturn(couponTypePage);

        // When
        Page<CouponTypeResponseDto> result = couponTypeService.getAllCouponType(pageable);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals("Book Discount", result.getContent().get(0).getCouponTypeName());
        assertEquals("Category Discount", result.getContent().get(1).getCouponTypeName());
        verify(couponTypeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("getCouponType : id가 존재")
    void getCouponType_ExistingId(){
        Long couponTypeId = 1L;
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(couponType1));

        CouponTypeResponseDto result = couponTypeService.getCouponType(couponTypeId);

        assertNotNull(result);
        assertEquals(couponTypeId, result.getCouponTypeId());
        assertEquals("Book Discount", result.getCouponTypeName());
        verify(couponTypeRepository, times(1)).findById(couponTypeId);
    }

    @Test
    @DisplayName("getCouponType : id가 존재하지 않음")
    void getCouponType_NonExistingId() {
        // Given
        Long couponTypeId = 99L;
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> couponTypeService.getCouponType(couponTypeId));
        verify(couponTypeRepository, times(1)).findById(couponTypeId);
    }

    @Test
    @DisplayName("getCouponByCouponPolicy : id가 유효함")
    void getCouponByCouponPolicy_ValidPolicyId(){
        // Given
        Long couponPolicyId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("couponTypeId").ascending());
        Page<CouponType> couponTypePage = new PageImpl<>(Arrays.asList(couponType1, couponType2), pageable, 2);

        when(couponTypeRepository.findByCouponPolicyCouponPolicyId(pageable, couponPolicyId)).thenReturn(couponTypePage);

        // When
        Page<CouponTypeResponseDto> result = couponTypeService.getCouponByCouponPolicy(pageable, couponPolicyId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Book Discount", result.getContent().get(0).getCouponTypeName());
        assertEquals("Category Discount", result.getContent().get(1).getCouponTypeName());
        verify(couponTypeRepository, times(1)).findByCouponPolicyCouponPolicyId(pageable, couponPolicyId);
    }

    @Test
    @DisplayName("getCouponByCouponPolicy : id가 유효하지 않음")
    void getCouponByCouponPolicy_InvalidPolicyId(){
        // Given
        Long invalidPolicyId = 0L;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> couponTypeService.getCouponByCouponPolicy(PageRequest.of(0, 10), invalidPolicyId));
        verify(couponTypeRepository, never()).findByCouponPolicyCouponPolicyId(any(), any());
    }

    @Test
    @DisplayName("createCouponType : bookCoupon 생성")
    void createCouponType_BookCoupon() {
        // Given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("Book Discount")
                .period(30)
                .deadline(null)
                .stacking(true)
                .couponPolicyId(1L)
                .couponTargetType(CouponTargetType.BOOK)
                .targetId(100L)
                .build();

        when(bookRepository.findByBookId(100L)).thenReturn(Optional.of(((BookCoupon) couponType1).getBook()));
        when(couponTypeRepository.save(any(BookCoupon.class))).thenReturn((BookCoupon) couponType1);

        // When
        CouponTypeResponseDto result = couponTypeService.createCouponType(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("Book Discount", result.getCouponTypeName());
        assertEquals(100L, result.getCouponTargetId());
        verify(bookRepository, times(1)).findByBookId(100L);
        verify(couponTypeRepository, times(1)).save(any(BookCoupon.class));
    }

    @Test
    @DisplayName("createCouponType : CategoryCoupon 생성")
    void createCouponType_CategoryCoupon() {
        // Given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("Category Discount")
                .period(null)
                .deadline(LocalDateTime.now().plusDays(15))
                .stacking(false)
                .couponPolicyId(1L)
                .couponTargetType(CouponTargetType.CATEGORY)
                .targetId(1001L)
                .build();

        when(categoryRepository.findById(1001L)).thenReturn(Optional.of(((CategoryCoupon) couponType2).getCategory()));
        when(couponTypeRepository.save(any(CategoryCoupon.class))).thenReturn((CategoryCoupon) couponType2);

        // When
        CouponTypeResponseDto result = couponTypeService.createCouponType(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("Category Discount", result.getCouponTypeName());
        assertEquals(1001L, result.getCouponTargetId());
        verify(categoryRepository, times(1)).findById(1001L);
        verify(couponTypeRepository, times(1)).save(any(CategoryCoupon.class));
    }

    @Test
    @DisplayName("createCouponType : AllCoupon 생성")
    void createCouponType_AllCouponRequest() {
        // Given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("All Discount")
                .period(null)
                .deadline(LocalDateTime.now().plusDays(10))
                .stacking(true)
                .couponPolicyId(2L)
                .couponTargetType(CouponTargetType.ALL)
                .build();

        when(couponTypeRepository.save(any(AllCoupon.class))).thenReturn((AllCoupon) couponType3);

        // When
        CouponTypeResponseDto result = couponTypeService.createCouponType(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("All Discount", result.getCouponTypeName());
        verify(couponTypeRepository, times(1)).save(any(AllCoupon.class));
    }

    @Test
    @DisplayName("createCouponType : Book이 없을 때")
    void createCouponType_NoBook() {
        // Given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("Book Discount")
                .period(null)
                .deadline(LocalDateTime.now().plusDays(30))
                .stacking(true)
                .couponPolicyId(1L)
                .couponTargetType(CouponTargetType.BOOK)
                .targetId(999L)
                .build();

        when(bookRepository.findByBookId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> couponTypeService.createCouponType(requestDto));
        verify(bookRepository, times(1)).findByBookId(999L);
        verify(couponTypeRepository, never()).save(any(CouponType.class));
    }

    @Test
    @DisplayName("createCouponType : Category 없을 때")
    void createCouponType_NoCategory() {
        // Given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("Category Discount")
                .period(15)
                .deadline(null)
                .stacking(false)
                .couponPolicyId(1L)
                .couponTargetType(CouponTargetType.CATEGORY)
                .targetId(999L)
                .build();

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> couponTypeService.createCouponType(requestDto));
        verify(categoryRepository, times(1)).findById(999L);
        verify(couponTypeRepository, never()).save(any(CouponType.class));
    }

    @Test
    @DisplayName("createCouponType : TargetId null")
    void createCouponType_NullTargetId() {
        // Given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("Invalid Coupon")
                .period(10)
                .deadline(null)
                .stacking(true)
                .couponPolicyId(1L)
                .couponTargetType(CouponTargetType.BOOK)
                .targetId(null)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> couponTypeService.createCouponType(requestDto));
        verify(bookRepository, never()).findByBookId(anyLong());
        verify(categoryRepository, never()).findById(anyLong());
        verify(couponTypeRepository, never()).save(any(CouponType.class));
    }

    @Test
    @DisplayName("deleteCouponType : 유효한 Id")
    void deleteCouponType_ValidId() {
        // Given
        Long couponTypeId = 1L;
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(couponType1));
        when(couponRepository.findByCouponTypeCouponTypeId(couponTypeId)).thenReturn(Collections.emptyList());

        // When
        couponTypeService.deleteCouponType(couponTypeId);

        // Then
        verify(couponTypeRepository, times(1)).delete(couponType1);
        verify(couponRepository, times(1)).findByCouponTypeCouponTypeId(couponTypeId);
    }

    @Test
    @DisplayName("deleteCouponType : CouponType이 없을 때")
    void deleteCouponType_NotExistingId() {
        // Given
        Long couponTypeId = 99L;
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> couponTypeService.deleteCouponType(couponTypeId));
        verify(couponTypeRepository, times(1)).findById(couponTypeId);
        verify(couponRepository, never()).findByCouponTypeCouponTypeId(couponTypeId);
    }

    @Test
    @DisplayName("deleteCouponType : CouponType이 이미 회원들에게 발급 됐을 때")
    void deleteCouponType_AlreadyIssuedCoupons() {
        // Given
        Long couponTypeId = 1L;
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(couponType1));
        when(couponRepository.findByCouponTypeCouponTypeId(couponTypeId)).thenReturn(Arrays.asList(mock(Coupon.class)));

        // When & Then
        assertThrows(AlreadyCouponTypeIssue.class, () -> couponTypeService.deleteCouponType(couponTypeId));
        verify(couponRepository, times(1)).findByCouponTypeCouponTypeId(couponTypeId);
        verify(couponTypeRepository, never()).delete(couponType1);
    }
}