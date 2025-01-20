package com.simsimbookstore.apiserver.users.user.service;

import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.dto.GuestUserRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserResponse;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    User updateUserStatus(Long userId, UserStatus userStatus);

    // 유저의 등급 계산 함수
    User updateUserGrade(Long userId, Tier tier);

    User updateUserLatestLoginDate(Long userId, LocalDateTime latestLoginDate);

    User getUser(Long userId);

    User getUserWithGradeAndRoles(Long userId);

    boolean existsUser(Long userId);

    List<UserResponse> getAllActiveUser();

    Tier getUserTier(Long userId);

    User createGuest(GuestUserRequestDto dto);

    List<UserResponse> getUserByBirthMonth(String monthStr);

    int updateDormantUserState(int period);


}
