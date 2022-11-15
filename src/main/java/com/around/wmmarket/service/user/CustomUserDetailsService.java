package com.around.wmmarket.service.user;

import com.around.wmmarket.controller.dto.user.UserSignInRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_role.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class  CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findWithUserRolesByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(email));
        return SignedUser.builder()
                .name(user.getEmail())
                .password(user.getPassword())
                .role(authorities(user.getUserRoles())).build();
    }

    public Collection<? extends GrantedAuthority> authorities(List<UserRole> userRoles){
        return userRoles.stream()
                .map(role -> "ROLE_"+role.getRole().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public SignedUser getSignedUser(UserSignInRequestDto requestDto){
        String email=requestDto.getEmail();
        String password=requestDto.getPassword();

        User user = userRepository.findWithUserRolesByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(email));
        return SignedUser.builder()
                .name(email)
                .password(password)
                .role(authorities(user.getUserRoles())).build(); // TODO : 매개변수로 써야할까
    }
}
