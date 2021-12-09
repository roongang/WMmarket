package com.around.wmmarket.controller.dto.User;

import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserSaveRequestDto {
    private String email;
    private String password;
    private MultipartFile image;
    private String nickname;
    private Role role;
    private String city_1;
    private String town_1;
    private String city_2;
    private String town_2;
}
