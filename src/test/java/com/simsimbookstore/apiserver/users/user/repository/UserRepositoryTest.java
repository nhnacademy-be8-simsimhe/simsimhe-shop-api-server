package com.simsimbookstore.apiserver.users.user.repository;

import com.netflix.discovery.converters.Auto;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private GradeRepository gradeRepository;

    LocalUser testUser;
    @BeforeEach
    void setUp() {
        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .pointRate(BigDecimal.valueOf(0.01))
                .build();

        Grade royalGrade = Grade.builder()
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .pointRate(BigDecimal.valueOf(0.01))
                .build();

        gradeRepository.save(standardGrade);
        gradeRepository.save(royalGrade);

        testUser = LocalUser.builder()
//                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .loginId("test")
                .password("test")
                .build();
        testUser = localUserRepository.save(testUser);
    }

    @Test
    void findById(){
        Optional<User> optionalUser = userRepository.findById(testUser.getUserId());
        assertTrue(optionalUser.isPresent());
        assertEquals(testUser.getUserId(), optionalUser.get().getUserId());
    }

    @Test
    void statusUpdate() {
        testUser.updateUserStatus(UserStatus.INACTIVE);
        User updatedUser = userRepository.save(testUser);

        assertEquals(updatedUser.getUserStatus(), UserStatus.INACTIVE);
    }

    @Test
    void gradeUpdate(){
        Grade newGrade = gradeRepository.findByTier(Tier.ROYAL);
        testUser.updateGrade(newGrade);

        User updatedUser = userRepository.save(testUser);
        assertEquals(updatedUser.getGrade().getTier(), Tier.ROYAL);
    }
}