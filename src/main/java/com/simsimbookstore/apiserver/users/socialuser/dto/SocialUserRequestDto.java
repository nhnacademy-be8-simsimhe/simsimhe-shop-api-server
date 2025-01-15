package com.simsimbookstore.apiserver.users.socialuser.dto;

import com.simsimbookstore.apiserver.users.user.entity.Gender;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class SocialUserRequestDto {
    private String oauthId;
    private String email;
    private String mobile;
    private String name;
    private Gender gender;
    private Provider provider;
}
