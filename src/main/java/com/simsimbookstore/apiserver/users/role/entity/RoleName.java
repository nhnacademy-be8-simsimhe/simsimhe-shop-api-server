package com.simsimbookstore.apiserver.users.role.entity;

import lombok.Getter;

@Getter
public enum RoleName {
    USER("일반회원"),
    GUEST("게스트"),
    ADMIN("관리자");

    private String role;

    RoleName(String role) {
        this.role = role;
    }
}
