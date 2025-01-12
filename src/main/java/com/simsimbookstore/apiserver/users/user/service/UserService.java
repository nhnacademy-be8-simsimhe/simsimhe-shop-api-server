package com.simsimbookstore.apiserver.users.user.service;

import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;

import java.time.LocalDateTime;

public interface UserService {
    User updateUserStatus(Long userId, UserStatus userStatus);

    User updateUserGrade(Long userId, Tier tier);

    User updateUserLatestLoginDate(Long userId, LocalDateTime latestLoginDate);

    User getUser(Long userId);

    User getUserWithGradeAndRoles(Long userId);

    boolean existsUser(Long userId);
}
