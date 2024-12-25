package com.simsimbookstore.apiserver.users.user.entity;


import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuperBuilder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", length = 50)
    private String userName;

    @Column(name = "mobile_number", length = 15, unique = true)
    private String mobileNumber;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    @Column
    private LocalDate birth;

    @Column(length = 6)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime latestLoginDate;

    @Column(name = "is_social_login", nullable = false)
    private boolean isSocialLogin = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRoleList = new HashSet<>();

    public void addUserRole(UserRole userRole) {
        userRoleList.add(userRole);
        userRole.setUser(this);
    }

    public void assignGrade(Grade grade){
        this.grade = grade;
    }


    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }
}
