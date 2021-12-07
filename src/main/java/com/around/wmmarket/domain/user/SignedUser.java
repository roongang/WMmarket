package com.around.wmmarket.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// TODO : signed user 위치 변경필요
@Getter
@RequiredArgsConstructor
public class SignedUser implements UserDetails {
    private String name;
    private String password;
    private Collection<? extends GrantedAuthority> role;

    @Builder
    public SignedUser(String name,String password,Collection<? extends GrantedAuthority> role){
        this.name=name;
        this.password=password;
        this.role=role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    // TODO : singedUser 로직 추가해줘야함
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
