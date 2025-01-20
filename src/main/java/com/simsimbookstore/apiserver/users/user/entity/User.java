package com.simsimbookstore.apiserver.users.user.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(nullable = false, length = 255)
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column
    private LocalDate birth;

    @Column(length = 6)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Builder.Default
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
        if (Objects.isNull(userRoleList)) {
            userRoleList = new HashSet<>();
        }
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

    public void updateLatestLoginDate(LocalDateTime latestLoginDate) {
        this.latestLoginDate = latestLoginDate;
    }
}
