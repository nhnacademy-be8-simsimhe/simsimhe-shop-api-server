package com.simsimbookstore.apiserver.users.address.repository;

import com.simsimbookstore.apiserver.users.address.entity.Address;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    // 유저 아이디로 검색 리스트 반환
    // 어드래스 아이디로 검색
    // save
    List<Address> findAllByUserUserId(Long userId);

}
