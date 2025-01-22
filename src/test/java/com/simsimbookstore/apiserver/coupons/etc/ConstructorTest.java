package com.simsimbookstore.apiserver.coupons.etc;

import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConstructorTest {

    @Test
    @DisplayName("Allcoupon 기본 생성자 테스트")
    void allCouponNoArgsConstructorTest() throws Exception {
        // AllCoupon 클래스의 생성자 가져오기
        Constructor<AllCoupon> constructor = AllCoupon.class.getDeclaredConstructor();

        // 접근 제어 우회
        constructor.setAccessible(true);

        // 생성자 호출로 객체 생성
        AllCoupon allCoupon = constructor.newInstance();

        // 객체가 생성되었는지 확인
        assertNotNull(allCoupon);
    }
}
