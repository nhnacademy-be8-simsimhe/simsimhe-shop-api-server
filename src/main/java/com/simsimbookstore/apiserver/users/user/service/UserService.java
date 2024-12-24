package com.simsimbookstore.apiserver.users.user.service;

import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.dto.UserGradeUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.dto.UserStatusUpdateRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;

public interface UserService {
    User updateUserStatus(Long userId, UserStatus userStatus);

    User updateUserGrade(Long userId, Tier tier);
}
