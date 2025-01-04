package com.simsimbookstore.apiserver.users.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserLatestLoginDateUpdateRequestDto {
    @NotNull
    LocalDateTime latestLoginDate;
}
