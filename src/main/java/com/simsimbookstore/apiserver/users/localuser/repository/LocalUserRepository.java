package com.simsimbookstore.apiserver.users.localuser.repository;

import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;



public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {
    LocalUser findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
