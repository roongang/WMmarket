package com.around.wmmarket.controller;

import com.around.wmmarket.config.SpringSecurityConfig;
import com.around.wmmarket.controller.dto.UserSaveRequestDto;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception{
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
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
        assertThat(allUser.get(0).getEmail()).isEqualTo(testEmail);
        //assertThat(allUser.get(0).getPassword()).isEqualTo(testPassword);
        assertThat(allUser.get(0).getNickname()).isEqualTo(testNickname);
        assertThat(allUser.get(0).getRole()).isEqualTo(testRole);

    }

}
