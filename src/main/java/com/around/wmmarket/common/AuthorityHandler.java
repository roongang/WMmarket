package com.around.wmmarket.common;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.domain.user_role.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthorityHandler {
    private final UserRepository userRepository;

    public void checkAuthorityToUser(SignedUser signedUser, int id) {
        if(signedUser == null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!signedUser.getUsername().equals(user.getEmail()) &&
                signedUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_"+ Role.ADMIN.name()))) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);
    }
}
