package com.simsimbookstore.apiserver.users.localuser.service;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;

public interface LocalUserService {
    LocalUser saveLocalUser(LocalUserRequestDto localUserRequestDto);

    LocalUser findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
