package com.around.wmmarket.domain.user_role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER,ADMIN;

    public String getName(){
        return name();
    }
}
