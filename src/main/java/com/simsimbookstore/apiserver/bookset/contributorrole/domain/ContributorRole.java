package com.simsimbookstore.apiserver.bookset.contributorrole.domain;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "contributor_roles")
public class ContributorRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contributor_role_id")
    private Long contributorRoleId;

    @Column(name = "contributor_role_name", nullable = false, length = 10)
    private String contributorRoleName;


}
