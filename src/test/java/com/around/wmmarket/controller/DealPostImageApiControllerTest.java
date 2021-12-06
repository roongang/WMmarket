package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostImage.DealPostImageSaveRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_post_image.DealPostImageRepository;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class) //junit4
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealPostImageApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private DealPostImageRepository dealPostImageRepository;

    private MockMvc mvc;

    User user;

    @BeforeTransaction
    public void makeUser(){
        user = User.builder()
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
        // TODO : 이미지 삭제하는거 추가하자.
        dealPostImageRepository.deleteAll();
        dealPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void DealPostImageSave() throws Exception{
        // given
        // save dealPost first
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        MockMultipartFile file1= new MockMultipartFile("files","img1.jpg","image/jpeg","img1".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2= new MockMultipartFile("files","img2.png","image/png","img2".getBytes(StandardCharsets.UTF_8));

        DealPostImageSaveRequestDto requestDto= new DealPostImageSaveRequestDto();
        DealPost dealPost=dealPostRepository.findAll().get(0);
        requestDto.setDealPostId(dealPost.getId());

        String url = "http://localhost:"+port+"/api/v1/dealPostImage";
        // when
        mvc.perform(multipart(url)
                .file(file1)
                .file(file2)
                .param("dealPostId",dealPost.getId().toString())
        ).andExpect(status().isOk());
    }

    @Test
    public void Test(){
        log.info("File.separator:"+ File.separator);
        log.info("File.separator x2:"+ File.separator+File.separator);
    }
}
