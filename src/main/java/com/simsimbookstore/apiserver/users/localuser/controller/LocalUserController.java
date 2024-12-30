package com.simsimbookstore.apiserver.users.localuser.controller;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/users/localUsers")
@RestController
public class LocalUserController {
    private final LocalUserService userService;

    @PostMapping
    public ResponseEntity<?> addLocalUser(
            @RequestBody @Valid LocalUserRegisterRequestDto localUserRequestDto
            ) {
        LocalUser response = userService.saveLocalUser(localUserRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{loginId}/exists")
    public ResponseEntity<?> existsByLoginId(@PathVariable String loginId) {
        boolean result = userService.existsByLoginId(loginId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{loginId}")
    public LocalUser getLocalUser(@PathVariable String loginId) {
        LocalUser localUser = userService.findByLoginId(loginId);
        return localUser;
    }
}
