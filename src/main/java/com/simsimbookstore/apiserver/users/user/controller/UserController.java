package com.simsimbookstore.apiserver.users.user.controller;

import com.simsimbookstore.apiserver.users.user.dto.UserGradeUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserStatusUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import com.simsimbookstore.apiserver.users.user.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // 유저 상태 업데이트
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long userId,
            @RequestBody @Valid UserStatusUpdateRequestDto userStatusUpdateRequestDto,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        User response = userService.updateUserStatus(userId, userStatusUpdateRequestDto.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{userId}/grade")
    public ResponseEntity<?> updateGrade(
            @PathVariable Long userId,
            @RequestBody @Valid UserGradeUpdateRequestDto userGradeUpdateRequestDto,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        User response = userService.updateUserGrade(userId, userGradeUpdateRequestDto.getTier());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
