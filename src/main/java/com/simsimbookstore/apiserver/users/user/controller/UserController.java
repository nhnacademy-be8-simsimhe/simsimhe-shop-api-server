package com.simsimbookstore.apiserver.users.user.controller;

import com.simsimbookstore.apiserver.users.UserMapper;
import com.simsimbookstore.apiserver.users.user.dto.UserGradeUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserLatestLoginDateUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserResponse;
import com.simsimbookstore.apiserver.users.user.dto.UserStatusUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import com.simsimbookstore.apiserver.users.user.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestBody @Valid UserStatusUpdateRequestDto requestDto
    ) {
        userService.updateUserStatus(userId, requestDto.getStatus());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{userId}/grade")
    public ResponseEntity<?> updateGrade(
            @PathVariable Long userId,
            @RequestBody @Valid UserGradeUpdateRequestDto requestDto
    ) {
        User response = userService.updateUserGrade(userId, requestDto.getTier());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{userId}/latestLoginDate")
    public ResponseEntity<?> updateLatestLoginDate(
            @PathVariable Long userId,
            @RequestBody @Valid UserLatestLoginDateUpdateRequestDto requestDto
    ) {
        User user = userService.updateUserLatestLoginDate(userId, requestDto.getLatestLoginDate());
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        User user = userService.getUserWithGradeAndRoles(userId);
        UserResponse userResponse = UserMapper.toResponse(user);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUser() {
        List<UserResponse> allActiveUser = userService.getAllActiveUser();
        return ResponseEntity.status(HttpStatus.OK).body(allActiveUser);
    }

    @GetMapping(params = "birthMonth")
    public ResponseEntity<List<UserResponse>> getAllUserByBirth(@RequestParam("birthMonth") String birthMonth) {
        List<UserResponse> userByBirthMonth = userService.getUserByBirthMonth(birthMonth);
        return ResponseEntity.status(HttpStatus.OK).body(userByBirthMonth);
    }

}
