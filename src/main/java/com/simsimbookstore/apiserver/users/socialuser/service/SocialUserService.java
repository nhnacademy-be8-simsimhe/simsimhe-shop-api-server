package com.simsimbookstore.apiserver.users.socialuser.service;


import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserRequestDto;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;

public interface SocialUserService {

    SocialUser loginSocialUser(SocialUserRequestDto socialUserRequestDto);
}
