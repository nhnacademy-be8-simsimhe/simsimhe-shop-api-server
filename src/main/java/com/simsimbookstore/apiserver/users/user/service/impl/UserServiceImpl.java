package com.simsimbookstore.apiserver.users.user.service.impl;

import static java.time.LocalDate.now;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.UserMapper;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;

import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import com.simsimbookstore.apiserver.users.user.dto.GuestUserRequestDto;
import com.simsimbookstore.apiserver.users.user.entity.Gender;

import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.dto.UserResponse;

import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import com.simsimbookstore.apiserver.users.user.service.UserService;

import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import java.time.LocalDateTime;

import java.util.HashSet;

import java.util.List;

import java.util.Optional;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final RoleService roleService;
    private final GradeService gradeService;

    @Transactional
    @Override
    public User updateUserStatus(Long userId, UserStatus userStatus) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("not found user with ID : " + userId);
        }

        User user = optionalUser.get();
        user.updateUserStatus(userStatus);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUserGrade(Long userId, Tier tier) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("not found user with ID : " + userId);
        }

        User user = optionalUser.get();
        Grade newGrade = gradeRepository.findByTier(tier);
        user.updateGrade(newGrade);

            return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUserLatestLoginDate(Long userId, LocalDateTime latestLoginDate) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("not found user with ID : " + userId);
        }

        User user = optionalUser.get();
        user.updateLatestLoginDate(latestLoginDate);
        userRepository.save(user);
        Optional<User> savedUser = userRepository.findUserWithGradeAndUserRoleListByUserId(user.getUserId());
        return savedUser.get();
    }


    @Override
    public User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with ID : " + userId));
        return user;
    }

    @Override
    public User getUserWithGradeAndRoles(Long userId){
        User user = userRepository.findUserWithGradeAndUserRoleListByUserId(userId)
                .orElseThrow(() -> new NotFoundException("not found user with ID : " + userId));
        return user;
    }

    @Override
    public boolean existsUser(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public List<UserResponse> getAllActiveUser() {
        List<User> users = userRepository.findAllByUserStatus(UserStatus.ACTIVE, RoleName.USER);
        return users.stream().map(UserMapper::toResponse).toList();
    }

    @Override
    public List<UserResponse> getUserByBirthMonth(String monthStr) {
        boolean isNumeric = monthStr.chars().allMatch(Character::isDigit);
        // 문자열이 숫자인지 확인
        if (!isNumeric) {
            throw new IllegalArgumentException("month는 숫자여야 합니다.");
        }
        // 1과 12사이 숫자인지 확인
        int month = Integer.parseInt(monthStr);
        if (month > 12 || month < 1) {
            throw new IllegalArgumentException("month는 1과 12 사이 숫자여야합니다.");
        }
        List<User> users = userRepository.findAllByBirthMonth(month, RoleName.USER);
        return users.stream().map(UserMapper::toResponse).toList();
    }

    public Tier getUserTier(Long userId) {
        return  (getUser(userId).getGrade().getTier());
    }


    @Transactional
    @Override
    public User createGuest(GuestUserRequestDto dto) {
        String uuid = UUID.randomUUID().toString().substring(0,15);


        Grade grade = gradeService.findByTier(Tier.STANDARD);

        User guest = User.builder()
                .userName(dto.getUserName())
                .mobileNumber(uuid)
                .email(uuid.substring(0,5)+"@"+uuid.substring(6,15)+"com")
                .birth(now())
                .gender(Gender.MALE)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(now().atStartOfDay())
                .latestLoginDate(now().atStartOfDay())
                .isSocialLogin(false)
                .userRoleList(new HashSet<>())
                .grade(grade)
                .build();

        Role role = roleService.findByRoleName(RoleName.GUEST);
        UserRole userRole = UserRole.builder()
                .role(role)
                .build();

        guest.addUserRole(userRole);
        userRepository.save(guest);

        return guest;
    }
}
