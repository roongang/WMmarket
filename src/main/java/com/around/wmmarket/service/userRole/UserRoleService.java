package com.around.wmmarket.service.userRole;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.userRole.UserRoleSaveResponseDto;
import com.around.wmmarket.domain.user_role.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_role.UserRole;
import com.around.wmmarket.domain.user_role.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserRoleSaveResponseDto save(SignedUser signedUser, Integer userId, List<Role> roles){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!user.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);
        // save
        List<UserRole> userRoles=roles.stream()
                .map(role -> UserRole.builder()
                        .user(user)
                        .role(role)
                        .build())
                .collect(Collectors.toList());
        return UserRoleSaveResponseDto.builder()
                .ids(userRoles.stream()
                        .map(UserRole::getId)
                        .collect(Collectors.toList()))
                .roles(userRoles.stream()
                        .map(UserRole::getRole)
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public int save(User user,Role role){
        return userRoleRepository.save(
                UserRole.builder()
                        .user(user)
                        .role(role)
                        .build()).getId();
    }

    @Transactional
    public void deleteAll(User user){
        userRoleRepository.deleteByUser(user);
    }
}
