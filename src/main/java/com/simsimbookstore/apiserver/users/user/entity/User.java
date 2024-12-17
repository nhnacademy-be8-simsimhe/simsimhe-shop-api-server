package com.simsimbookstore.apiserver.users.user.entity;


import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    @Column(name = "mobile_number", nullable = false, length = 15, unique = true)
    private String mobileNumber;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false, length = 6)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVE;

    private LocalDateTime created_at;

    private LocalDateTime latest_login_date;

    @Column(name = "is_social_login", nullable = false)
    private boolean isSocialLogin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;


}
