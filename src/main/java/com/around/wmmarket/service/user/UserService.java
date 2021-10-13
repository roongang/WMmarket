package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.UserSaveRequestDto;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void signUp(UserSaveRequestDto requestDto){
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .image(requestDto.getImage())
                .nickname(requestDto.getNickname())
                .role(requestDto.getRole())
                .build();
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(email));

        // TODO : 권한 수정해야함
        List<GrantedAuthority> roles=new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),roles);
    }
}
