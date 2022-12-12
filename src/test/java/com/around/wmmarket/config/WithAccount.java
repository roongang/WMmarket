package com.around.wmmarket.config;

import com.around.wmmarket.domain.user_role.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFactory.class)
public @interface WithAccount {
    String email();
    Role[] roles() default {Role.USER};
}
