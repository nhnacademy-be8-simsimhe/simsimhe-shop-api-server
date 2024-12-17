package com.simsimbookstore.apiserver.users.oauthuser.entity;


import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "oauth_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("OauthUser")
public class OauthUser extends User {

    @Column(name = "oauth_id", nullable = false, length = 50, unique = true)
    private String oauthId;
}
