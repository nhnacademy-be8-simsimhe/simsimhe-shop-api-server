package com.simsimbookstore.apiserver.users.localuser.service;

import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;

public interface LocalUserService {
    LocalUser saveLocalUser(LocalUserRegisterRequestDto localUserRequestDto);

    LocalUser findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
