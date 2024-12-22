package com.simsimbookstore.apiserver.users.role.service;

import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;

public interface RoleService {
    Role save(Role role);

    Role findByRoleName(RoleName roleName);
}
