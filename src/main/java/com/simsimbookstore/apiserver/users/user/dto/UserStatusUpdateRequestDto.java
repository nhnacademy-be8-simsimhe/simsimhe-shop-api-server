package com.simsimbookstore.apiserver.users.user.dto;

import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class UserStatusUpdateRequestDto {

    @NotNull
    private UserStatus status;
}
