package com.simsimbookstore.apiserver.users.localuser.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.repository.RoleRepository;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class LocalUserRepositoryTest {

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private RoleRepository roleRepository;

    private LocalUser testUser;

    @BeforeEach
    void setUp(){
        Grade grade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .pointRate(BigDecimal.valueOf(0.01))
                .build();

        gradeRepository.save(grade);

        testUser = LocalUser.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(grade)
                .loginId("test")
                .password("test")
                .build();

        localUserRepository.save(testUser);
    }

    @Test
    void findByLoginId() {
        LocalUser foundUser = localUserRepository.findByLoginId("test");
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(testUser.getLoginId(), foundUser.getLoginId());
        Assertions.assertEquals(testUser.getEmail(), foundUser.getEmail());
        Assertions.assertEquals(testUser.getPassword(), foundUser.getPassword());

        Assertions.assertNull(localUserRepository.findByLoginId("not exist loginId"));
    }

    @Test
    void existsByLoginId() {
        Assertions.assertTrue(localUserRepository.existsByLoginId("test"));
        Assertions.assertFalse(localUserRepository.existsByLoginId("not exist loginId"));
    }
}