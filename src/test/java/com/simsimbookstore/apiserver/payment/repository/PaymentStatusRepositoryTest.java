package com.simsimbookstore.apiserver.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Field;

@DataJpaTest(excludeAutoConfiguration = {EntityManagerFactoryDependsOnPostProcessor.class})
@ActiveProfiles("test")
public class PaymentStatusRepositoryTest {

    @MockitoBean
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Test
    void findByPaymentStatusNameTest() throws NoSuchFieldException {
        PaymentStatus status = new PaymentStatus();

        for (Field field : status.getClass().getDeclaredFields()) {
            field.setAccessible(true);
        }

        ReflectionUtils.setField(status.getClass().getDeclaredField("paymentStatusName"), status, "SUCCESS");

        paymentStatusRepository.findByPaymentStatusName("SUCCESS");
    }
}
