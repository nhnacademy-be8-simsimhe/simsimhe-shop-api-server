package com.simsimbookstore.apiserver.users.localuser.entity;


import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "local_users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@DiscriminatorValue("LocalUser")
public class LocalUser extends User {

    @Column(name = "login_id", nullable = false, length = 20, unique = true)
    private String loginId;

    @Column(nullable = false, length = 20)
    private String password;


}
