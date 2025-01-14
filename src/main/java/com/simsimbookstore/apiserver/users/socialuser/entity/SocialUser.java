package com.simsimbookstore.apiserver.users.socialuser.entity;


import com.simsimbookstore.apiserver.users.socialuser.dto.Provider;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "social_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("SocialUser")
public class SocialUser extends User {

    @Column(name = "oauth_id", nullable = false, length = 50, unique = true)
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private Provider provider;
}
