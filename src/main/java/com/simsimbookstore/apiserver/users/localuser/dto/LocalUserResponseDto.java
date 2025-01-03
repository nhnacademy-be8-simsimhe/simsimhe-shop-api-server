package com.simsimbookstore.apiserver.users.localuser.dto;

import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LocalUserResponseDto {
    private Long userId;

    private List<RoleName> roles;

    private String loginId;

    private String password;
}