package com.simsimbookstore.apiserver.users.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(QuerydslConfig.class)
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
                .build();

        Grade royalGrade = Grade.builder()
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
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
    void updateStatus() {
        testUser.updateUserStatus(UserStatus.INACTIVE);
        User updatedUser = userRepository.save(testUser);

        assertEquals(updatedUser.getUserStatus(), UserStatus.INACTIVE);
    }

    @Test
    void updateGrade(){
        Grade newGrade = gradeRepository.findByTier(Tier.ROYAL);
        testUser.updateGrade(newGrade);

        User updatedUser = userRepository.save(testUser);
        assertEquals(updatedUser.getGrade().getTier(), Tier.ROYAL);
    }

    @Test
    void updateLatestLoginDate() {
        testUser.updateLatestLoginDate(LocalDateTime.now().plusHours(1));
        User updatedUser = userRepository.save(testUser);

        assertEquals(testUser.getLatestLoginDate(), updatedUser.getLatestLoginDate());
    }

    @Test
    void findUserWithGradeById() {
        Optional<User> user = userRepository.findById(testUser.getUserId());
        assertTrue(user.isPresent());

        assertEquals(user.get().getGrade().getTier(), Tier.STANDARD);
    }
}