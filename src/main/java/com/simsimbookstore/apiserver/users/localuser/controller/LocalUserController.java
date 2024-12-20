package com.simsimbookstore.apiserver.users.localuser.controller;

import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users/localUsers")
@RestController
public class LocalUserController {
    private final LocalUserService userService;

    public LocalUserController(LocalUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addLocalUser(
            @RequestBody @Valid LocalUserRequestDto localUserRequestDto,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        LocalUser response = userService.saveLocalUser(localUserRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{loginId}/exists")
    public boolean existsByLoginId(@PathVariable String loginId) {
        return userService.existsByLoginId(loginId);
    }
}
