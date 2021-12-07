package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.User.UserSigninRequestDto;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class  CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(email));
        return SignedUser.builder()
                .name(user.getEmail())
                .password(user.getPassword())
                .role(authorities(user.getRole())).build();
    }

    // 임시로 role 하나만 만들게 설정
    public Collection<? extends GrantedAuthority> authorities(Role role){
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_"+role.toString()));
    }

    public SignedUser getSignedUser(UserSigninRequestDto requestDto){
        String email=requestDto.getEmail();
        String password=requestDto.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(email));
        return SignedUser.builder()
                .name(email)
                .password(password)
                .role(authorities(user.getRole())).build(); // TODO : 매개변수로 써야할까
    }
}
