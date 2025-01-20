package com.simsimbookstore.apiserver.users.user.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .build();

        royalGrade = Grade.builder()
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        testUser = User.builder()
                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .latestLoginDate(LocalDateTime.now())
                .grade(standardGrade)
                .build();

        UserRole build = UserRole.builder()
                .role(Role.builder().roleId(1L).roleName(RoleName.USER).build())
                .build();
        testUser.addUserRole(build);
    }

    @Test
    void getUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User user = userService.getUser(1L);

        verify(userRepository, times(1)).findById(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void getUserTier(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.getUserTier(1L);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserWithGradeAndRoles() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(userRepository.findUserWithGradeAndUserRoleListByUserId(1L)).thenReturn(Optional.of(testUser));
        User user = userService.getUser(1L);

        verify(userRepository, times(1)).findById(1L);

        when(userRepository.findUserWithGradeAndUserRoleListByUserId(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserWithGradeAndRoles(1L));
    }

    @Test
    void getAllActiveUser() {
        when(userRepository.findAllByUserStatus(any(), any())).thenReturn(List.of(testUser));

        userService.getAllActiveUser();
        verify(userRepository, times(1)).findAllByUserStatus(UserStatus.ACTIVE, RoleName.USER);
    }

    @Test
    void existsUser(){
        userService.existsUser(1L);
        verify(userRepository, times(1)).existsById(anyLong());
    }



    @Test
    void updateUserStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.updateUserStatus(1L, UserStatus.INACTIVE);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserStatus(99L, UserStatus.INACTIVE));
    }

    @Test
    void updateUserGrade() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(gradeRepository.findByTier(Tier.ROYAL)).thenReturn(royalGrade);

        userService.updateUserGrade(1L, Tier.ROYAL);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserGrade(99L, Tier.ROYAL));
    }

    @Test
    void updateUserLatestLoginDate() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserWithGradeAndUserRoleListByUserId(1L)).thenReturn(Optional.of(testUser));

        userService.updateUserLatestLoginDate(1L, LocalDateTime.now());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserWithGradeAndUserRoleListByUserId(1L);
        verify(userRepository, times(1)).save(testUser);

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserLatestLoginDate(99L, LocalDateTime.now()));
    }
}