//package com.simsimbookstore.apiserver.users.controller;
//
//import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
//import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
//import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
//import org.springframework.web.bind.annotation.*;
//
//@RequestMapping("/api/users")
//@RestController
//public class UserController {
//    private final LocalUserService userService;
//
//    public UserController(LocalUserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/localUsers")
//    public LocalUser addLocalUser(@RequestBody LocalUserRequestDto localUserRequestDto) {
//        LocalUser response = userService.saveLocalUser(localUserRequestDto);
//        return response;
//    }
//
////    @PostMapping("/socialUsers")
////    public SocialUser addSocialUser(@RequestBody SocialUser socialUser) {
////        SocialUser response = userService.saveSocialUser(socialUser);
////        return response;
////    }
//
//    @GetMapping("/localUsers/{loginId}/exists")
//    public boolean existsByLoginId(@PathVariable String loginId) {
//        return userService.existsByLoginId(loginId);
//    }
//}
