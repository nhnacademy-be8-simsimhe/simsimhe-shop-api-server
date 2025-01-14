package com.simsimbookstore.apiserver.users.socialuser.dto;

import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SocialUserResponse {

    private Long userId;

    private List<RoleName> roles;

    private UserStatus userStatus;

    private LocalDateTime latestLoginDate;

    private String oauthId;

    private Provider provider;
}
