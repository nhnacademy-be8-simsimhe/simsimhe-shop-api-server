package com.simsimbookstore.apiserver.users.controller;

import com.simsimbookstore.apiserver.users.localuser.LocalUser;
import com.simsimbookstore.apiserver.users.socialuser.SocialUser;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/shop")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/localUsers")
    public LocalUser addLocalUser(@RequestBody LocalUser localUser) {
        LocalUser response = userService.saveLocalUser(localUser);
        return response;
    }

    @PostMapping("users/socialUsers")
    public SocialUser addSocialUser(@RequestBody SocialUser socialUser) {
        SocialUser response = userService.saveSocialUser(socialUser);
        return response;
    }

    @GetMapping("/users/localUsers/{loginId}/count")
    public int getLocalUserCount(@PathVariable String loginId) {
        int count = userService.countByLoginId(loginId);
        return count;
    }
}
