package com.simsimbookstore.apiserver.users.localuser.service.impl;

import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRegisterRequestDto;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.mapper.LocalUserMapper;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LocalUserServiceImpl implements LocalUserService {


    private final LocalUserRepository localUserRepository;

    private final RoleService roleService;
    private final GradeService gradeService;
    private final PointHistoryService pointHistoryService;
    private final CouponService couponService;


    @Transactional
    @Override
    public LocalUser saveLocalUser(LocalUserRegisterRequestDto localUserRequestDto) {
        Grade grade = gradeService.findByTier(localUserRequestDto.getTier());

        if (localUserRepository.existsByLoginId(localUserRequestDto.getLoginId())) {
            throw new AlreadyExistException("already exist login Id: " + localUserRequestDto.getLoginId());
        }

        Role role = roleService.findByRoleName(RoleName.USER);
        LocalUser localUser = LocalUserMapper.registerRequestDtoTo(localUserRequestDto);
        localUser.assignGrade(grade);

        UserRole userRole = UserRole.builder()
                .role(role)
                .build();

        localUser.addUserRole(userRole);

        LocalUser save = localUserRepository.save(localUser);

        pointHistoryService.signupPoint(save);

        // 회원가입 시 welcome 쿠폰을 발급
        couponService.issueCoupons(List.of(save.getUserId()), CouponType.WELCOME_COUPON_TYPE_ID);

        return localUser;
    }

    @Override
    public LocalUser findByLoginId(String loginId) {
        return localUserRepository.findByLoginId(loginId);
    }

    //중복 loginId 체크
    @Override
    public boolean existsByLoginId(String loginId) {
        return localUserRepository.existsByLoginId(loginId);
    }
}