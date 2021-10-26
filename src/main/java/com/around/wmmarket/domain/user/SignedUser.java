package com.around.wmmarket.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class SignedUser implements UserDetails {
    private int id;
    private String password;
    private String name;
    private Collection<? extends GrantedAuthority> role;
    private String tokenId;

    @Builder
    public SignedUser(int id,String password,String name,Collection<? extends GrantedAuthority> role){
        this.id=id;
        this.password=password;
        this.name=name;
        this.role=role;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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
