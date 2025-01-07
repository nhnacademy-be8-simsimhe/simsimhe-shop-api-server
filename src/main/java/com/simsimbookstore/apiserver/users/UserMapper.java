package com.simsimbookstore.apiserver.users;

import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.dto.UserResponse;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        List<RoleName> roles = new ArrayList<>();
        for (UserRole userRole : user.getUserRoleList()){
            roles.add(userRole.getRole().getRoleName());
        }

        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .birth(user.getBirth())
                .gender(user.getGender())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .latestLoginDate(user.getLatestLoginDate())
                .isSocialLogin(user.isSocialLogin())
                .tier(user.getGrade().getTier())
                .roles(roles)
                .build();
        return userResponse;
    }
}
