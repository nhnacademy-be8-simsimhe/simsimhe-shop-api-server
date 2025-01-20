package com.simsimbookstore.apiserver.users.localuser.controller;

import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserResponseDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.mapper.LocalUserMapper;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/users/localUsers")
@RestController
public class LocalUserController {
    private final LocalUserService localUserService;

    @PostMapping
    public ResponseEntity<LocalUser> addLocalUser(
            @RequestBody @Valid LocalUserRegisterRequestDto localUserRequestDto
    ) {
        LocalUser response = localUserService.saveLocalUser(localUserRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{loginId}/exists")
    public ResponseEntity<Boolean> existsByLoginId(@PathVariable String loginId) {
        boolean response = localUserService.existsByLoginId(loginId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<LocalUserResponseDto> getLocalUser(@PathVariable String loginId) {
        LocalUser localUser = localUserService.findByLoginId(loginId);
        if (Objects.isNull(localUser)) {
            return ResponseEntity.ok(null);
        }

        LocalUserResponseDto response = LocalUserMapper.localUserResponseDtoTo(localUser);
        return ResponseEntity.ok(response);
    }
}
