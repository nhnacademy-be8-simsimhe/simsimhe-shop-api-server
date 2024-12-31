package com.simsimbookstore.apiserver.users.role.service.impl;

import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.repository.RoleRepository;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(Role role){
        if (roleRepository.existsByRoleName(role.getRoleName())){
            throw new AlreadyExistException("already exists " + role.getRoleName());
        }

        return roleRepository.save(role);
    }

    @Override
    public Role findByRoleName(RoleName roleName){
        Role role = roleRepository.findByRoleName(RoleName.USER);
        if (Objects.isNull(role)) {
            throw new NotFoundException("Role with name " + RoleName.USER + " does not exist");
        }

        return roleRepository.findByRoleName(roleName);
    }

    public boolean existsByRoleName(RoleName roleName){
        return roleRepository.existsByRoleName(roleName);
    }
}
