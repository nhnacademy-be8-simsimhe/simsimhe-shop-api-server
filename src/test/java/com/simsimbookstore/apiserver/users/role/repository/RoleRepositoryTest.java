package com.simsimbookstore.apiserver.users.role.repository;

import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .roleName(RoleName.USER)
                .build();

        roleRepository.save(testRole);
    }

    @Test
    void findByRoleName() {
        Role role = roleRepository.findByRoleName(RoleName.USER);
        assertNotNull(role);
    }

    @Test
    void existsByRoleName() {
        assertTrue(roleRepository.existsByRoleName(RoleName.USER));
        assertFalse(roleRepository.existsByRoleName(RoleName.GUEST));
    }
}