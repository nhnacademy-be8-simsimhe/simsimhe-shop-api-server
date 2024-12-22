package com.simsimbookstore.apiserver.users.user.repository;


import com.simsimbookstore.apiserver.users.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
