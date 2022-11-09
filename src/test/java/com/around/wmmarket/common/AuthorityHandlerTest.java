package com.around.wmmarket.common;

import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AuthorityHandlerTest {
    @Autowired
    private AuthorityHandler authorityHandler;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void checkAuthorityToUserEqualEmailTest() {
        // given
        User user=userRepository.save(User.builder()
                .email("user@email")
                .password("password")
                .nickname("user nickname")
                .build());

        SignedUser signedUser=SignedUser.builder()
                .name("user@email")
                .password("password")
                .role(null)
                .build();
        // when
        authorityHandler.checkAuthorityToUser(signedUser,user.getId());
        // then
    }
}
