package com.simsimbookstore.apiserver.users.user.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    Grade standardGrade;
    Grade royalGrade;

    @BeforeEach
    void setUp() {
        standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        royalGrade = Grade.builder()
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
                .latestLoginDate(LocalDateTime.now().minusDays(100))
                .build();
        testUser = localUserRepository.save(testUser);
    }

    @Test
    void findById() {
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
    void updateGrade() {
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

    @Test
    void updateUserStateInactive() {
        // 휴면 유저임
        User testUser1 = LocalUser.builder()
                .userName("test2")
                .email("test2@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .loginId("test2")
                .password("test2")
                .latestLoginDate(LocalDateTime.now().minusDays(31))
                .build();

        // 휴면 유저가 아님
        User testUser2 = LocalUser.builder()
                .userName("test1")
                .email("test1@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .loginId("test1")
                .password("test1")
                .latestLoginDate(LocalDateTime.now())
                .build();

        userRepository.save(testUser1);
        userRepository.save(testUser2);

        int count = userRepository.updateUserStateInactive(LocalDateTime.now().minusDays(30));
        assertEquals(count, 2);

        Optional<User> optionalUser = userRepository.findById(testUser.getUserId());
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        assertEquals(UserStatus.INACTIVE, user.getUserStatus());

        Optional<User> optionalUser1 = userRepository.findById(testUser1.getUserId());
        assertTrue(optionalUser1.isPresent());
        User user1 = optionalUser1.get();
        assertEquals(UserStatus.INACTIVE, user1.getUserStatus());

        Optional<User> optionalUser2 = userRepository.findById(testUser2.getUserId());
        assertTrue(optionalUser2.isPresent());
        User user2 = optionalUser2.get();
        assertEquals(UserStatus.ACTIVE, user2.getUserStatus());
    }

    @Test
    void updateUserGrade() {
        int i = userRepository.updateUserGrade(testUser.getUserId(), royalGrade);

        assertEquals(1, i);
        Optional<User> optionalUser = userRepository.findById(testUser.getUserId());
        assertTrue(optionalUser.isPresent());

        assertEquals(royalGrade.getGradeId(), optionalUser.get().getGrade().getGradeId());
    }
}