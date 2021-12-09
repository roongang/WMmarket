package com.around.wmmarket.controller.dto.User;

import com.around.wmmarket.domain.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    private String password;
    private String nickname;
    private Role role;
    private String city_1;
    private String town_1;
    private String city_2;
    private String town_2;
}
