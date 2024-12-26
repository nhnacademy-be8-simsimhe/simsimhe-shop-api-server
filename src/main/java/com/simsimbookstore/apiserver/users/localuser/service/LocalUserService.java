package com.simsimbookstore.apiserver.users.localuser.service;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserLoginRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;

public interface LocalUserService {
    LocalUser saveLocalUser(LocalUserRequestDto localUserRequestDto);

    LocalUser findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
