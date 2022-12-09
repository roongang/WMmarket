package com.around.wmmarket.controller;

import com.around.wmmarket.config.WithAccount;
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
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
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
    public void makeDealSuccess(User seller,User buyer){
        DealPost dealPost=DealPost.builder()
                .user(seller)
                .category(Category.A)
                .title("title")
                .price(50000)
                .content("content")
                .dealState(DealState.ONGOING).build();
        dealPostRepository.save(dealPost);
        dealPost.setDealState(DealState.DONE);
        DealSuccess dealSuccess=dealSuccessRepository.save(DealSuccess.builder()
                .dealPost(dealPost)
                .buyer(buyer).build());
        dealPost.setDealSuccess(dealSuccess);
    }

    public DealReview makeDealReview(User seller,User buyer){
        DealPost dealPost=dealPostRepository.save(DealPost.builder()
                .user(seller)
                .category(Category.A)
                .title("title")
                .price(50000)
                .content("content")
                .dealState(DealState.ONGOING).build());
        makeDealSuccess(seller,buyer);
        return dealReviewRepository.save(DealReview.builder()
                .seller(seller)
                .buyer(buyer)
                .content("review_content")
                .dealPost(dealPost)
                .build());
    }
    @Before
    public void setup(){
        mvc= MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @After
    public void tearDown(){
        dealReviewRepository.deleteAll();
        dealSuccessRepository.deleteAll();
        dealPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithAccount(email = "buyer@email")
    public void dealReviewSave() throws Exception{
        // given
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .nickname("seller")
                .password("password").build());
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
        makeDealSuccess(seller,buyer);

        DealReviewSaveRequestDto requestDto=DealReviewSaveRequestDto.builder()
                .dealPostId(dealPostRepository.findAll().get(0).getId())
                .content("review_content").build();
        String url="http://localhost:"+port+"/api/v1/deal-reviews";
        // when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
        // then
        assertThat(dealReviewRepository.findAll().isEmpty()).isFalse();
    }

    @Test
    @Transactional
    public void dealReviewGet() throws Exception{
        // given
        User buyer=userRepository.save(User.builder()
                .email("buyer@email")
                .nickname("buyer")
                .password("password").build());
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .nickname("seller")
                .password("password").build());
        DealReview dealReview=makeDealReview(seller,buyer);

        String url="http://localhost:"+port+"/api/v1/deal-reviews/"+dealReview.getId();
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
    @WithAccount(email = "buyer@email")
    public void dealReviewPut() throws Exception{
        // given
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .nickname("seller")
                .password("password").build());;
        DealReview dealReview=makeDealReview(seller,buyer);

        DealReviewUpdateRequestDto requestDto=DealReviewUpdateRequestDto.builder()
                .content("update_review")
                .build();
        String url="http://localhost:"+port+"/api/v1/deal-reviews/"+dealReview.getId();
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
    @WithAccount(email = "buyer@email")
    public void dealReviewDelete() throws Exception{
        // given
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .nickname("seller")
                .password("password").build());
        DealReview dealReview=makeDealReview(seller,buyer);

        String url="http://localhost:"+port+"/api/v1/deal-reviews/"+dealReview.getId();
        // when
        mvc.perform(delete(url)
                .param("dealReviewId",dealReviewRepository.findAll().get(0).getId().toString()))
                .andExpect(status().isOk());
        // then
        assertThat(dealReviewRepository.findAll().isEmpty()).isTrue();
    }
}
