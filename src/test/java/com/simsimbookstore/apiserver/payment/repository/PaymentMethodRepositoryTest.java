package com.simsimbookstore.apiserver.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Field;

@DataJpaTest(excludeAutoConfiguration = {EntityManagerFactoryDependsOnPostProcessor.class})
@ActiveProfiles("test")
class PaymentMethodRepositoryTest {

    @MockitoBean
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    void findByPaymentMethodTest() throws NoSuchFieldException {

        // paymentMethod 객체 만들어서 넣어줌
        PaymentMethod paymentMethod = new PaymentMethod();
        // ddl로 데이터 삽입 > setter X , reflection 사용
        for(Field field : paymentMethod.getClass().getDeclaredFields()){
            field.setAccessible(true);
        }

        ReflectionUtils.setField(paymentMethod.getClass().getDeclaredField("paymentMethod"), paymentMethod, "CARD" );

        paymentMethodRepository.findByPaymentMethod("CARD");
    }
}