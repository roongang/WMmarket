package com.around.wmmarket.domain.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "authentication")
@Entity
public class Authentication {

    @Id
    private Integer userId;

    @Column(nullable = false)
    private Integer isAuth;

    @Column(nullable = false)
    private String code;

    @Builder
    public Authentication(Integer isAuth, String code){
        this.isAuth=isAuth;
        this.code=code;
    }
}