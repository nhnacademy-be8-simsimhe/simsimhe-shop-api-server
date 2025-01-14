package com.simsimbookstore.apiserver.users.socialuser.controller;


import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserRequestDto;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserResponse;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import com.simsimbookstore.apiserver.users.socialuser.mapper.SocialUserMapper;
import com.simsimbookstore.apiserver.users.socialuser.service.SocialUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/users/socialUser")
@RestController
public class SocialUserController {
    private final SocialUserService socialUserService;

    @PostMapping("/login")
    public ResponseEntity<?> loginSocialUser(
            @RequestBody SocialUserRequestDto socialUserRequestDto
    ) {
        SocialUser socialUser = socialUserService.loginSocialUser(socialUserRequestDto);
        SocialUserResponse socialUserResponse = SocialUserMapper.toSocialUserResponse(socialUser);
        return ResponseEntity.ok(socialUserResponse);
    }
}
