package com.around.wmmarket.config;

import com.around.wmmarket.controller.dto.user.UserSaveRequestDto;
import com.around.wmmarket.domain.user_role.Role;
import com.around.wmmarket.service.user.CustomUserDetailsService;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String email = withAccount.email();
        List<String> roles = new ArrayList<>();
        for (Role role : withAccount.roles()) roles.add(role.name());

        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto();
        userSaveRequestDto.setEmail(email);
        userSaveRequestDto.setPassword("password");
        userSaveRequestDto.setNickname(email+" nickname");
        userSaveRequestDto.setImage(new MockMultipartFile("image","img.jpg","image/jpeg","img".getBytes(StandardCharsets.UTF_8)));
        userSaveRequestDto.setRoles(roles);
        userService.save(userSaveRequestDto);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserDetails principal = customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal, "password"));
        context.setAuthentication(authentication);

        return context;
    }
}

