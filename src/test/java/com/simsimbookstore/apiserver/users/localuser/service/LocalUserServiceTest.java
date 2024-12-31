package com.simsimbookstore.apiserver.users.localuser.service;

import com.simsimbookstore.apiserver.users.exception.DuplicateIdException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.mapper.LocalUserMapper;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.localuser.service.impl.LocalUserServiceImpl;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserServiceTest {

    @InjectMocks
    private LocalUserServiceImpl localUserService;

    @Mock
    private LocalUserRepository localUserRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private GradeService gradeService;

    LocalUserRequestDto testUser;
    Grade testGrade;
    UserRole testUserRole;

    @BeforeEach
    void setUp() {
        testUser = LocalUserRequestDto.builder()
                .userName("John Doe")
                .mobileNumber("01051278121")
                .email("johndoe@example.com")
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .tier(Tier.GOLD)
                .loginId("test")
                .password("test")
                .build();

        testGrade = Grade.builder()
                .gradeId(1L)
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        Role testRole = Role.builder()
                .roleName(RoleName.USER)
                .build();

        testUserRole = UserRole.builder()
                .role(testRole)
                .build();
    }

    @Test
    @DisplayName("로컬 유저 저장 테스트")
    void testSaveLocalUser() {
        when(roleService.findByRoleName(RoleName.USER)).thenReturn(new Role(1L,RoleName.USER));

        localUserService.saveLocalUser(testUser);

        verify(localUserRepository, times(1)).save(any(LocalUser.class));
    }

    @Test
    @DisplayName("로컬 유저 저장시 중복 아이디 에러")
    void testSaveLocalUserDuplicate() {
        when(localUserRepository.existsByLoginId(testUser.getLoginId())).thenReturn(true);

        assertThrows(DuplicateIdException.class, () -> localUserService.saveLocalUser(testUser));
    }

    @Test
    @DisplayName("로그인 아이디로 로컬 유저 조회 테스트")
    void findByLoginId() {
        LocalUser actualUser = LocalUserMapper.requestDtoTo(testUser);
        actualUser.assignGrade(testGrade);
        actualUser.addUserRole(testUserRole);
        when(localUserRepository.findByLoginId(testUser.getLoginId())).thenReturn(actualUser);
        LocalUser expectUser = localUserService.findByLoginId(testUser.getLoginId());

        verify(localUserRepository, times(1)).findByLoginId(testUser.getLoginId());
        assertEquals(actualUser.getUserName(), expectUser.getUserName());
    }

    @Test
    @DisplayName("로그인 아이디 중복 테스트")
    void existsByLoginId() {
        // Case: 로그인 아이디가 존재하지 않는 경우
        when(localUserRepository.existsByLoginId("not found Login Id")).thenReturn(false);

        boolean resultFalse = localUserService.existsByLoginId("not found Login Id");
        assertFalse(resultFalse);

        // Case: 로그인 아이디가 존재 하는 경우
        when(localUserRepository.existsByLoginId(testUser.getLoginId())).thenReturn(true);

        boolean resultTrue = localUserService.existsByLoginId(testUser.getLoginId());
        assertTrue(resultTrue);

        verify(localUserRepository, times(1)).existsByLoginId(testUser.getLoginId());
    }
}