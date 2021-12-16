package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.dealReview.DealReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_review.DealReviewRepository;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.deal_success.DealSuccessRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealReviewApiControllerTest {
    @LocalServerPort int port;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealReviewRepository dealReviewRepository;
    @Autowired
    private DealSuccessRepository dealSuccessRepository;

    private MockMvc mvc;

    /*거래글 리뷰를 남기려면
    1. 유저1 회원가입 및 로그인
    2. 유저1 글 작성
    3. 유저1 글 DONE 변경
    4. 유저2 회원가입 및 로그인
    5. 유저2 리뷰 작성
    */
    @BeforeTransaction
    public void setting(){
        if(userRepository.existsByEmail("user1_email")) return;
        User user1= User.builder()
                .email("user1_email")
                .password(passwordEncoder.encode("user1_pw"))
                .nickname("user1_nn")
                .role(Role.USER).build();
        userRepository.save(user1);
        User user2=User.builder()
                .email("user2_email")
                .password(passwordEncoder.encode("user2_pw"))
                .nickname("user2_nn")
                .role(Role.USER).build();
        userRepository.save(user2);

        DealPost dealPost=DealPost.builder()
                .user(user1)
                .category(Category.A)
                .title("title")
                .price(50000)
                .content("content")
                .dealState(DealState.ONGOING).build();
        dealPost.setDealState(DealState.DONE);
        DealSuccess dealSuccess=DealSuccess.builder()
                .dealPost(dealPost)
                .buyer(user2).build();
        dealSuccessRepository.save(dealSuccess);
        dealPost.setDealSuccess(dealSuccess);
        dealPostRepository.save(dealPost);
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
        dealReviewRepository.deleteAll();
        dealSuccessRepository.deleteAll();
        dealPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user2_email")
    public void dealReviewSave() throws Exception{
        // given
        DealReviewSaveRequestDto requestDto=DealReviewSaveRequestDto.builder()
                .dealPostId(dealPostRepository.findAll().get(0).getId())
                .content("review_content").build();
        String url="http://localhost:"+port+"/api/v1/dealReview";
        // when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        assertThat(dealReviewRepository.findAll().isEmpty()).isFalse();
    }

    @Test
    @Transactional
    public void dealReviewGet() throws Exception{
        // given
        DealPost dealPost=dealPostRepository.findAll().get(0);
        User user2=userRepository.findByEmail("user2_email")
                .orElseThrow(()->new UsernameNotFoundException("user2_email not found"));
        DealReview dealReview=DealReview.builder()
                .seller(dealPost.getUser())
                .buyer(user2)
                .content("review_content")
                .dealPost(dealPost)
                .build();
        dealReviewRepository.save(dealReview);

        String url="http://localhost:"+port+"/api/v1/dealReview";
        // when
        MvcResult result=mvc.perform(get(url)
                .param("dealReviewId",dealReviewRepository.findAll().get(0).getId().toString()))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains("review_content");
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user2_email")
    public void dealReviewPut() throws Exception{
        // given
        DealPost dealPost=dealPostRepository.findAll().get(0);
        User user2=userRepository.findByEmail("user2_email")
                .orElseThrow(()->new UsernameNotFoundException("user2_email not found"));
        DealReview dealReview=DealReview.builder()
                .seller(dealPost.getUser())
                .buyer(user2)
                .content("review_content")
                .dealPost(dealPost)
                .build();
        dealReviewRepository.save(dealReview);

        DealReviewUpdateRequestDto requestDto=DealReviewUpdateRequestDto.builder()
                .dealReviewId(dealReviewRepository.findAll().get(0).getId())
                .content("update_review")
                .build();
        String url="http://localhost:"+port+"/api/v1/dealReview";
        // when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        assertThat(dealReviewRepository.findAll().get(0).getContent()).isEqualTo("update_review");
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user2_email")
    public void dealReviewDelete() throws Exception{
        // given
        DealPost dealPost=dealPostRepository.findAll().get(0);
        User user2=userRepository.findByEmail("user2_email")
                .orElseThrow(()->new UsernameNotFoundException("user2_email not found"));
        DealReview dealReview=DealReview.builder()
                .seller(dealPost.getUser())
                .buyer(user2)
                .content("review_content")
                .dealPost(dealPost)
                .build();
        dealReviewRepository.save(dealReview);

        String url="http://localhost:"+port+"/api/v1/dealReview";
        // when
        mvc.perform(delete(url)
                .param("dealReviewId",dealReviewRepository.findAll().get(0).getId().toString()))
                .andExpect(status().isOk());
        // then
        assertThat(dealReviewRepository.findAll().isEmpty()).isTrue();
    }
}
