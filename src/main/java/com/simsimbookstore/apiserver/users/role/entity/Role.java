package com.simsimbookstore.apiserver.users.role.entity;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private RoleName roleName;


}
