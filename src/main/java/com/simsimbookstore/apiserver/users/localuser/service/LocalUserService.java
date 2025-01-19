package com.simsimbookstore.apiserver.users.localuser.service;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;

public interface LocalUserService {
    LocalUser saveLocalUser(LocalUserRegisterRequestDto localUserRequestDto);

    LocalUser findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
