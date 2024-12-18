package com.simsimbookstore.apiserver.books.contributor.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Setter
@Getter
@Table(name = "contributors")
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contributor_id")
    private Long contributorId;

    @Column(name = "contributor_name", nullable = false, length = 10)
    private String contributorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "contributor_role", nullable = false)
    private ContributorRole contributorRole;
}
