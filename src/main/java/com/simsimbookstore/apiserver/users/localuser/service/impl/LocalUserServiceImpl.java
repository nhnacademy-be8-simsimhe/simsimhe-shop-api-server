package com.simsimbookstore.apiserver.users.localuser.service.impl;

import com.simsimbookstore.apiserver.users.exception.DuplicateIdException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
import com.simsimbookstore.apiserver.users.localuser.mapper.LocalUserMapper;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalUserServiceImpl implements LocalUserService {

    private final LocalUserRepository localUserRepository;

    private final RoleService roleService;
    private final GradeService gradeService;

    public LocalUserServiceImpl(LocalUserRepository localUserRepository, RoleService roleService, GradeService gradeService) {
        this.localUserRepository = localUserRepository;
        this.roleService = roleService;
        this.gradeService = gradeService;
    }

    @Transactional
    @Override
    public LocalUser saveLocalUser(LocalUserRequestDto localUserRequestDto) {
        Grade grade = gradeService.findByTier(localUserRequestDto.getTier());

        if (localUserRepository.existsByLoginId(localUserRequestDto.getLoginId())) {
            throw new DuplicateIdException("already exist login Id: " + localUserRequestDto.getLoginId());
        }

        Role role = roleService.findByRoleName(RoleName.USER);
        LocalUser localUser = LocalUserMapper.requestDtoTo(localUserRequestDto);
        localUser.assignGrade(grade);

        UserRole userRole = UserRole.builder()
                .role(role)
                .build();

        localUser.addUserRole(userRole);

        localUserRepository.save(localUser);

        return localUser;
    }

    @Override
    @Transactional
    public LocalUser findByLoginId(String loginId) {
        LocalUser localuser = localUserRepository.findByLoginId(loginId);
        return localuser;
    }


    //중복 loginId 체크
    @Transactional
    @Override
    public boolean existsByLoginId(String loginId) {
        return localUserRepository.existsByLoginId(loginId);
    }
}
