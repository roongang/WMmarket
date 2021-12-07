package com.around.wmmarket.controller.dto.User;

import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {
    private String email;
    private String password;
    private String image;
    private String nickname;
    private Role role;

    @Builder
    public UserSaveRequestDto(String email,String password,String image,String nickname,Role role){
        this.email=email;
        this.password=password;
        this.image=image;
        this.nickname=nickname;
        this.role=role;
    }
}
