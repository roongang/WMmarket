package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.User.UserGetResponseDto;
import com.around.wmmarket.controller.dto.User.UserSaveRequestDto;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
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

    public boolean isExist(String email){
        return userRepository.existsByEmail(email);
    }

    public UserGetResponseDto getUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        System.out.println(user.getEmail());
        return UserGetResponseDto.builder()
                .email(user.getEmail())
                .image(user.getImage())
                .nickname(user.getNickname())
                .role(user.getRole())
                .city_1(user.getCity_1())
                .town_1(user.getTown_1())
                .city_2(user.getCity_2())
                .town_2(user.getTown_2())
                .isAuth(user.getIsAuth())
                .code(user.getCode())
                .build();
    }
}
