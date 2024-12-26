package com.simsimbookstore.apiserver.users.user.service.impl;

import com.simsimbookstore.apiserver.users.exception.ResourceNotFoundException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.dto.UserGradeUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserStatusUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GradeRepository gradeRepository;

    User testUser;

    Grade standardGrade;
    Grade royalGrade;

    @BeforeEach
    void setUp() {
        standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .pointRate(BigDecimal.valueOf(0.01))
                .build();

        royalGrade = Grade.builder()
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .pointRate(BigDecimal.valueOf(0.01))
                .build();

        testUser = User.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    }

    @Test
    void updateUserStatus() {

        userService.updateUserStatus(1L, UserStatus.INACTIVE);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.updateUserStatus(99L, UserStatus.INACTIVE));
    }

    @Test
    void updateUserGrade() {
        when(gradeRepository.findByTier(Tier.ROYAL)).thenReturn(royalGrade);

        userService.updateUserGrade(1L, Tier.ROYAL);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.updateUserGrade(99L, Tier.ROYAL));
    }
}