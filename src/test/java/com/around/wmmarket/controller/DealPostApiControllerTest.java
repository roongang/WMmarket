package com.around.wmmarket.controller;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_success.DealSuccessRepository;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.dealPost.DealPostService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import java.time.LocalDateTime;

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
    // repo
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private DealSuccessRepository dealSuccessRepository;
    // service
    @Autowired
    private DealPostService dealPostService;

    private MockMvc mvc;

    private User user;

    @BeforeTransaction
    public void makeUser(){
        if(userRepository.existsByEmail("user@email")) return;
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
        // repo delete
        dealSuccessRepository.deleteAll();
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
        requestDto.setCategory(Category.A.name());
        requestDto.setTitle("title");
        requestDto.setPrice(1000);
        requestDto.setContent("content");
        String url = "http://localhost:"+port+"/api/v1/deal-posts";
        // when
        mvc.perform(multipart(url)
                .file(file1)
                .file(file2)
                .param("category",requestDto.getCategory())
                .param("title",requestDto.getTitle())
                .param("price",requestDto.getPrice().toString())
                .param("content",requestDto.getContent())
        ).andExpect(status().isCreated());
        // then
        assertThat(dealPostRepository.findAll()).isNotNull();
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
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId;
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        String content=result.getResponse().getContentAsString();
        assertThat(content).contains("userId");
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostIncreaseViewCntTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .title("title")
                .category(Category.A)
                .content("content")
                .price(1000)
                .dealState(DealState.ONGOING)
                .build());
        DealPost dealPost=dealPostRepository.findAll().get(0);
        int dealPostId=dealPost.getId();
        int beforeViewCnt=dealPost.getViewCnt();
        int updateViewCnt=1;
        DealPostUpdateRequestDto requestDto=DealPostUpdateRequestDto.builder()
                .viewCnt(updateViewCnt)
                .build();
        String url="http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId+"/view-cnt";
        // when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        dealPost=dealPostRepository.findAll().get(0);
        assertThat(beforeViewCnt+updateViewCnt).isEqualTo(dealPost.getViewCnt());
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
        User buyer=User.builder()
                .email("buyer@email")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname1")
                .role(Role.USER).build();
        userRepository.save(buyer);
        int buyerId=userRepository.findByEmail("buyer@email").orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND))
                .getId();
        DealPostUpdateRequestDto requestDto=DealPostUpdateRequestDto.builder()
                .category(Category.B.name())
                .content("update_content")
                .buyerId(buyerId)
                .dealState(DealState.DONE.name()).build();
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId;
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
    public void dealPostUpdateSuccessSaveTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        int dealPostId=dealPostRepository.findAll().get(0).getId();

        userRepository.save(User.builder()
                .email("user@email2")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname2")
                .role(Role.USER).build());
        User user2=userRepository.findByEmail("user@email2")
                .orElseThrow(()->new UsernameNotFoundException("user@email2 없음"));

        DealPostUpdateRequestDto requestDto=DealPostUpdateRequestDto.builder()
                .category(Category.B.name())
                .content("update_content")
                .buyerId(user2.getId())
                .dealState(DealState.DONE.name()).build();
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId;
        // when
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        assertThat(dealSuccessRepository.findAll().get(0).getBuyer()).isEqualTo(user2);
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostUpdateSuccessUpdateTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        int dealPostId=dealPostRepository.findAll().get(0).getId();

        userRepository.save(User.builder()
                .email("user@email3")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname3")
                .role(Role.USER).build());
        User user3=userRepository.findByEmail("user@email3")
                .orElseThrow(()->new UsernameNotFoundException("user@email3 없음"));

        DealPostUpdateRequestDto requestDto=DealPostUpdateRequestDto.builder()
                .category(Category.B.name())
                .content("update_content")
                .buyerId(user3.getId())
                .dealState(DealState.DONE.name()).build();
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId;
        // when
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        assertThat(dealSuccessRepository.findAll().get(0).getBuyer()).isEqualTo(user3);
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostUpdateSuccessDeleteTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        int dealPostId=dealPostRepository.findAll().get(0).getId();

        userRepository.save(User.builder()
                .email("user@email2")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname2")
                .role(Role.USER).build());
        User user2=userRepository.findByEmail("user@email2")
                .orElseThrow(()->new UsernameNotFoundException("user@email2 없음"));

        DealPostUpdateRequestDto requestDto2=DealPostUpdateRequestDto.builder()
                .category(Category.B.name())
                .content("update_content")
                .buyerId(user2.getId())
                .dealState(DealState.DONE.name()).build();
        SignedUser signedUser=SignedUser.builder()
                .name("user@email")
                .build();
        dealPostService.update(signedUser,dealPostId,requestDto2);

        DealPostUpdateRequestDto requestDto=DealPostUpdateRequestDto.builder()
                .category(Category.B.name())
                .content("update_content")
                .dealState(DealState.ONGOING.name()).build();
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId;
        // when
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        assertThat(dealSuccessRepository.findAll().isEmpty()).isTrue();
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
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId;
        // when
        mvc.perform(delete(url)
                .param("dealPostId",Integer.toString(dealPostId)))
                .andExpect(status().isOk());
        // then
        assertThat(dealPostRepository.findAll().isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostPullTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        DealPost dealPost=dealPostRepository.findAll().get(0);
        int dealPostId=dealPost.getId();
        int beforePullingCnt=dealPost.getPullingCnt();
        LocalDateTime beforePullingDate=dealPost.getPullingDate();
        String url = "http://localhost:"+port+"/api/v1/deal-posts/"+dealPostId+"/pulling";
        // when
        mvc.perform(put(url))
                .andExpect(status().isOk());
        // then
        dealPost=dealPostRepository.findAll().get(0);
        assertThat(dealPost.getPullingCnt()).isEqualTo(beforePullingCnt+1);
        assertThat(dealPost.getPullingDate()).isAfterOrEqualTo(beforePullingDate);
    }
}
