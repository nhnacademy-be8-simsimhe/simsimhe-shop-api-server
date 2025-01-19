package com.simsimbookstore.apiserver.users.user.repository;


import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoleList ur JOIN ur.role r WHERE u.userStatus = :status AND r.roleName = :roleName")
    List<User> findAllByUserStatus(@Param("status") UserStatus status, @Param("roleName") RoleName roleName);

    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoleList ur JOIN ur.role r WHERE u.birth IS NOT NULL AND MONTH(u.birth) = :month AND r.roleName = :roleName")
    List<User> findAllByBirthMonth(@Param("month") int month, @Param("roleName") RoleName roleName);


    @EntityGraph(attributePaths = {"grade", "userRoleList", "userRoleList.role"})
    Optional<User> findUserWithGradeAndUserRoleListByUserId(Long userId);

    // 휴면 유저 체크용
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.userStatus = 'INACTIVE' WHERE u.userStatus = 'ACTIVE' AND u.latestLoginDate <= :targetDateTime")
    int updateUserStateInactive(@Param("targetDateTime") LocalDateTime targetDateTime);
}
