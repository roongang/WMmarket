package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.UserLoginRequestDto;
import com.around.wmmarket.controller.dto.UserSaveRequestDto;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockHttpSession session;

    private MockMvc mvc;

    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        session = new MockHttpSession();
    }

    @After
    public void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    public void UserSave() throws Exception{
        // given
        String testEmail="test_email";
        String testPassword="test_password";
        String testNickname="test_nickname";
        Role testRole=Role.USER;
        UserSaveRequestDto requestDto = UserSaveRequestDto.builder()
                .email(testEmail)
                .password(testPassword)
                .nickname(testNickname)
                .role(testRole)
                .build();
        String url = "http://localhost:"+port+"/api/v1/user";
        // when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
            .andExpect(status().isOk());
        // then
        List<User> allUser = userRepository.findAll();
        assertThat(allUser.get(0).getId()).isEqualTo(1);
        assertThat(allUser.get(0).getEmail()).isEqualTo(testEmail);
        log.info("user password = "+allUser.get(0).getPassword());
        assertThat(allUser.get(0).getNickname()).isEqualTo(testNickname);
        assertThat(allUser.get(0).getRole()).isEqualTo(testRole);
    }

    @Test
    public void UserSignIn() throws Exception{
        // given
        String testEmail="test_email";
        String testPassword="test_password";
        String testNickname="test_nickname";
        Role testRole=Role.USER;
        userRepository.save(User.builder()
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .nickname(testNickname)
                .role(testRole)
                .build());

        UserLoginRequestDto requestDto = UserLoginRequestDto.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
        String url = "http://localhost:"+port+"/api/v1/user/signIn";
        // when
        mvc.perform(post(url)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isOk())
                .andReturn();
        // then
        Object object = session.getAttribute("SPRING_SECURITY_CONTEXT");
        SecurityContextImpl context = (SecurityContextImpl) object;
        SignedUser signedUser = (SignedUser) context.getAuthentication().getPrincipal();
        assertThat(testEmail).isEqualTo(signedUser.getUsername());
        assert(passwordEncoder.matches(testPassword,signedUser.getPassword()));
    }
}
