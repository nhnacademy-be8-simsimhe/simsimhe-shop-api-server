package com.simsimbookstore.apiserver.users.user.service;

import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.socialuser.repository.SocialUserRepository;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import com.simsimbookstore.apiserver.users.localuser.LocalUser;
import com.simsimbookstore.apiserver.users.socialuser.SocialUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;

    public UserServiceImpl(UserRepository userRepository, LocalUserRepository localUserRepository, SocialUserRepository socialUserRepository) {
        this.userRepository = userRepository;
        this.localUserRepository = localUserRepository;
        this.socialUserRepository = socialUserRepository;
    }

    @Override
    public LocalUser saveLocalUser(LocalUser localUser) {
        localUserRepository.save(localUser);
        return localUser;
    }

    @Override
    public SocialUser saveSocialUser(SocialUser socialUser) {
        socialUserRepository.save(socialUser);
        return socialUser;
    }

    @Override
    public Optional<LocalUser> findByLoginId(String loginId){
        LocalUser localuser = localUserRepository.findByLoginId(loginId);
        return Optional.ofNullable(localuser);
    }

    @Override
    public int countByLoginId(String loginId){
        return localUserRepository.countByLoginId(loginId);
    }
}
