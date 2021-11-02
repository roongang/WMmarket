package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.UserSaveRequestDto;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void save(UserSaveRequestDto requestDto){
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .image(requestDto.getImage())
                .nickname(requestDto.getNickname())
                .role(requestDto.getRole())
                .build();
        userRepository.save(user);
    }
}
