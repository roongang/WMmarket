package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.UserLoginRequestDto;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(email));

        return SignedUser.builder()
                .id(user.getId())
                .password(user.getPassword())
                .role(authorities(user.getRole())).build();
    }

    // 임시로 role 하나만 만들게 설정
    private Collection<? extends GrantedAuthority> authorities(Role role){
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_"+role.toString()));
    }

    public SignedUser getSignedUser(UserLoginRequestDto requestDto,HttpSession session){
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException(username));
        return SignedUser.builder()
                .id(user.getId())
                .password(user.getPassword())
                .role(authorities(user.getRole())).build();
    }
}
