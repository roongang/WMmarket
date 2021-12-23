package com.around.wmmarket.controller.dto.user;

import com.around.wmmarket.domain.user.Role;
import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class UserGetResponseDto {
    // password 빠져있음
    private String email;
    private String nickname;
    private Role role;
    private String city_1;
    private String town_1;
    private String city_2;
    private String town_2;
    private Integer isAuth;
    private String code;
}
