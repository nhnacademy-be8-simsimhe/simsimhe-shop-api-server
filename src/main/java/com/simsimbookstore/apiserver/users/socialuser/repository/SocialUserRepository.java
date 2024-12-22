package com.simsimbookstore.apiserver.users.socialuser.repository;

import com.simsimbookstore.apiserver.users.socialuser.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
}
