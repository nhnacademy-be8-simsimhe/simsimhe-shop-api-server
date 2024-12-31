package com.simsimbookstore.apiserver.users.user.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    public UserServiceImpl(UserRepository userRepository, GradeRepository gradeRepository) {
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

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

    @Override
    public User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with ID : " + userId));
        return user;
    }

    @Override
    public boolean existsUser(Long userId) {
        return userRepository.existsById(userId);
    }
}
