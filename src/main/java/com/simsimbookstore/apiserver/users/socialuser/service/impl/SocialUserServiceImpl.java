package com.simsimbookstore.apiserver.users.socialuser.service.impl;

import com.simsimbookstore.apiserver.exception.BadRequestException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.role.service.RoleService;
import com.simsimbookstore.apiserver.users.socialuser.dto.SocialUserRequestDto;
import com.simsimbookstore.apiserver.users.socialuser.entity.SocialUser;
import com.simsimbookstore.apiserver.users.socialuser.mapper.SocialUserMapper;
import com.simsimbookstore.apiserver.users.socialuser.repository.SocialUserRepository;
import com.simsimbookstore.apiserver.users.socialuser.service.SocialUserService;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SocialUserServiceImpl implements SocialUserService {
    private final SocialUserRepository socialUserRepository;
    private final GradeService gradeService;
    private final RoleService roleService;

    @Override
    public SocialUser loginSocialUser(SocialUserRequestDto socialUserRequestDto) {
        if (Objects.isNull(socialUserRequestDto)) {
            throw new BadRequestException("social");
        }

        String oauthId = socialUserRequestDto.getOauthId();

        boolean isExist = socialUserRepository.existsByOauthId(socialUserRequestDto.getOauthId());
        if (isExist) {
            Optional<SocialUser> optionalSocialUser = socialUserRepository.findByOauthId(oauthId);
            return optionalSocialUser
                    .orElseThrow(() -> new NotFoundException("not found socialUser with oauthId: " + oauthId));
        } else { // 소셜 유저 최초 등록
            SocialUser socialUser = SocialUserMapper.toSocialUser(socialUserRequestDto);
            Grade tier = gradeService.findByTier(Tier.STANDARD);
            socialUser.assignGrade(tier);

            Role role = roleService.findByRoleName(RoleName.USER);
            UserRole userRole = UserRole.builder()
                    .role(role)
                    .build();
            socialUser.addUserRole(userRole);
            socialUser.updateLatestLoginDate(LocalDateTime.now());
            SocialUser savedSocialUser = socialUserRepository.save(socialUser);

            return savedSocialUser;
        }
    }
}
