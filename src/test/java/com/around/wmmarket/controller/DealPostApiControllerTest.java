package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.DealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @Autowired
    private DealPostRepository dealPostRepository;

    private MockMvc mvc;

    private User user;

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
                .alwaysDo(print())
                .build();
    }
    @AfterTransaction
    public void tearDown(){
        dealPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostSave() throws Exception{
        // given
        // multipart/form-data 형태이므로 setter 적용
        MockMultipartFile file1= new MockMultipartFile("files","img1.jpg","image/jpeg","img1".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2= new MockMultipartFile("files","img2.png","image/png","img2".getBytes(StandardCharsets.UTF_8));

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

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostGetTest() throws Exception{
        // given
        DealPost dealPost=DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build();
        dealPostRepository.save(dealPost);
        int dealPostId=dealPostRepository.findAll().get(0).getId();
        String url = "http://localhost:"+port+"/api/v1/dealPost";
        // when
        MvcResult result=mvc.perform(get(url)
                .param("dealPostId",Integer.toString(dealPostId)))
                .andExpect(status().isOk())
                .andReturn();
        // then
        String content=result.getResponse().getContentAsString();
        assertThat(content).contains("user@email");
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostUpdateTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        int dealPostId=dealPostRepository.findAll().get(0).getId();
        DealPostUpdateRequestDto requestDto=DealPostUpdateRequestDto.builder()
                .dealPostId(dealPostId)
                .category(Category.B)
                .content("update_content")
                .dealState(DealState.DONE).build();
        String url = "http://localhost:"+port+"/api/v1/dealPost";
        // when
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        DealPost dealPost=dealPostRepository.findAll().get(0);
        assertThat(dealPost.getCategory()).isEqualTo(Category.B);
        assertThat(dealPost.getContent()).isEqualTo("update_content");
        assertThat(dealPost.getDealState()).isEqualTo(DealState.DONE);
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostDeleteTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        int dealPostId=dealPostRepository.findAll().get(0).getId();
        String url = "http://localhost:"+port+"/api/v1/dealPost";
        // when
        mvc.perform(delete(url)
                .param("dealPostId",Integer.toString(dealPostId)))
                .andExpect(status().isOk());
        // then
        assertThat(dealPostRepository.findAll().isEmpty()).isTrue();
    }
}
