package com.simsimbookstore.apiserver.users.socialuser.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import com.simsimbookstore.apiserver.users.socialuser.dto.Provider;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class SocialUserRepositoryTest {
    @Autowired
    private SocialUserRepository socialUserRepository;
    @Autowired
    private GradeRepository gradeRepository;

    private SocialUser testSocialUser;

    @BeforeEach
    void setUp() {
        Grade testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        gradeRepository.save(testGrade);

        testSocialUser = SocialUser.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .oauthId("testOauthId")
                .provider(Provider.PAYCO)
                .build();

        testSocialUser = socialUserRepository.save(testSocialUser);

    }

    @Test
    void findByOauthId() {
        Optional<SocialUser> optionalSocialUser = socialUserRepository.findByOauthId(testSocialUser.getOauthId());
        assertTrue(optionalSocialUser.isPresent());
        assertEquals(optionalSocialUser.get().getOauthId(), testSocialUser.getOauthId());
    }

    @Test
    void existsById(){
        boolean isExist = socialUserRepository.existsByOauthId(testSocialUser.getOauthId());
        assertTrue(isExist);

        isExist = socialUserRepository.existsByOauthId("invalid OauthId");
        assertFalse(isExist);
    }
}