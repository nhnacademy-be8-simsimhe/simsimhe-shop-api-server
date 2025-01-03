package com.simsimbookstore.apiserver.users.user.repository;


import com.simsimbookstore.apiserver.users.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"grade", "userRoleList", "userRoleList.role"})
    public Optional<User> findUserWithGradeAndUserRoleListByUserId(Long userId);
}
