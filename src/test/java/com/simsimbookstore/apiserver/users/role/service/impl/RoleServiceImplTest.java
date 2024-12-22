package com.simsimbookstore.apiserver.users.role.service.impl;

import com.simsimbookstore.apiserver.users.exception.DuplicateId;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.repository.RoleRepository;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    Role testRole;
    @BeforeEach
    void setUp() {

        testRole = Role.builder()
                .roleName(RoleName.USER)
                .build();
    }

    @Test
    @DisplayName("새로운 역할 저장 테스트")
    void save() {
        when(roleRepository.existsByRoleName(RoleName.USER)).thenReturn(false);
        roleService.save(testRole);
        verify(roleRepository, times(1)).save(testRole);
    }

    @Test
    @DisplayName("중복된 새로운 역할 저장시 에러 테스트")
    void saveDuplicate() {
        when(roleRepository.existsByRoleName(RoleName.USER)).thenReturn(true);
        assertThrows(DuplicateId.class,()-> roleService.save(testRole));
    }

    @Test
    @DisplayName("역할 조회 테스트")
    void findByRoleName() {
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(testRole);
        Role role = roleService.findByRoleName(RoleName.USER);
        assertNotNull(role);
        assertEquals(testRole.getRoleName(), role.getRoleName());
    }
}