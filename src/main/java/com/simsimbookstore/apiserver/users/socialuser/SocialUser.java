package com.simsimbookstore.apiserver.users.socialuser;


import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

}
