package com.simsimbookstore.apiserver.users.user.service;

import com.simsimbookstore.apiserver.users.localuser.LocalUser;
import com.simsimbookstore.apiserver.users.socialuser.SocialUser;

import java.util.Optional;

public interface UserService {
    LocalUser saveLocalUser(LocalUser localUser);

    SocialUser saveSocialUser(SocialUser socialUser);

    Optional<LocalUser> findByLoginId(String loginId);

    int countByLoginId(String loginId);
}
