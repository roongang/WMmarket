package com.around.wmmarket.controller.dto;

import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {
    private String email;
    private String password;
    private String image;
    private String nickname;
    private Role role;
}
