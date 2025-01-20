package com.simsimbookstore.apiserver.users.user.dto;

import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserGradeUpdateRequestDto {

    @NotNull
    private Tier tier;
}
