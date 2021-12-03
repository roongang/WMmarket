package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.user.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealPostApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;

    @BeforeTransaction
    public void makeUser(){
        User user = User.builder()
                .email("user@email")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname")
                .role(Role.USER).build();
        userRepository.save(user);
    }
    @Before
    public void setup(){
        mvc= MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }
    @AfterTransaction
    public void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void DealPostSave() throws Exception{
        // given
        // multipart/form-data 형태이므로 setter 적용
        MockMultipartFile file1= new MockMultipartFile("multipartFiles","img1.jpg","image/jpeg","img1".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2= new MockMultipartFile("multipartFiles","img2.png","image/png","img2".getBytes(StandardCharsets.UTF_8));

        DealPostSaveRequestDto requestDto = new DealPostSaveRequestDto();
        requestDto.setCategory(Category.A);
        requestDto.setTitle("title");
        requestDto.setPrice(1000);
        requestDto.setContent("content");
        requestDto.setDealState(DealState.ONGOING);
        String url = "http://localhost:"+port+"/api/v1/dealPost";
        // when
        mvc.perform(multipart(url)
                .file(file1)
                .file(file2)
                .param("category",requestDto.getCategory().name())
                .param("title",requestDto.getTitle())
                .param("price",requestDto.getPrice().toString())
                .param("content",requestDto.getContent())
                .param("dealState",requestDto.getDealState().toString())
        ).andExpect(status().isOk()); // then
    }
}
