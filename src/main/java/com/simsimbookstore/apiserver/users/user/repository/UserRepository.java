package com.simsimbookstore.apiserver.users.user.repository;


import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUserStatus(UserStatus userStatus);

    @Query("SELECT u FROM User u WHERE u.birth IS NOT NULL AND MONTH(u.birth) = :month")
    List<User> findAllByBirthMonth(@Param("month") int month);


    @EntityGraph(attributePaths = {"grade", "userRoleList", "userRoleList.role"})
    Optional<User> findUserWithGradeAndUserRoleListByUserId(Long userId);
}
