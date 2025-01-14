package com.simsimbookstore.apiserver.users.socialuser.repository;

import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    @EntityGraph(attributePaths = {"grade", "userRoleList", "userRoleList.role"})
    Optional<SocialUser> findByOauthId(String oauthId);
    boolean existsByOauthId(String oauthId);
}
