package com.around.wmmarket.controller;

import com.around.wmmarket.config.WithAccount;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.deal_success.DealSuccessRepository;
import com.around.wmmarket.domain.manner_review.Manner;
import com.around.wmmarket.domain.manner_review.MannerReview;
import com.around.wmmarket.domain.manner_review.MannerReviewRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MannerReviewApiControllerTest {
    @LocalServerPort int port;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private DealSuccessRepository dealSuccessRepository;
    @Autowired
    private MannerReviewRepository mannerReviewRepository;

    private MockMvc mvc;

    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @After
    public void tearDown(){
        // repo delete
        mannerReviewRepository.deleteAll();
        dealSuccessRepository.deleteAll();
        dealPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    public void makeSuccessDealPost(User buyer,User seller) {
        dealSuccessRepository.save(DealSuccess.builder()
                .buyer(buyer)
                .dealPost(dealPostRepository.save(DealPost.builder()
                        .user(seller)
                        .price(1000)
                        .content("content")
                        .title("title")
                        .category(Category.A)
                        .dealState(DealState.DONE).build()))
                .build());
    }

    @Test
    @Transactional
    @WithAccount(email = "buyer@email")
    public void mannerReviewSaveTest() throws Exception{
        // given
        User seller=userRepository.save(User.builder()
                .email("seller@seller")
                .nickname("seller")
                .password("password")
                .build());
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("buyer@email"));

        makeSuccessDealPost(buyer,seller);

        MannerReviewSaveRequestDto requestDto=MannerReviewSaveRequestDto.builder()
                .sellerId(seller.getId())
                .buyerId(buyer.getId())
                .manner("GOOD_KIND").build();

        String url="http://localhost:"+port+"/api/v1/manner-reviews";
        // when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
        // then
        assertThat(mannerReviewRepository.findAll()).isNotNull();
    }

    @Test
    @Transactional
    public void mannerReviewGetTest() throws Exception{
        // given
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .nickname("seller")
                .password("password").build());
        User buyer=userRepository.save(User.builder()
                .email("buyer@buyer")
                .nickname("buyer")
                .password("password").build());

        makeSuccessDealPost(buyer,seller);
        MannerReview mannerReview=mannerReviewRepository.save(MannerReview.builder()
                .buyer(buyer)
                .seller(seller)
                .manner(Manner.GOOD_KIND).build());

        String url="http://localhost:"+port+"/api/v1/manner-reviews/"+mannerReview.getId();
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains("buyerId");
    }

    @Test
    @Transactional
    @WithAccount(email = "buyer@email")
    public void mannerReviewDeleteTest() throws Exception{
        // given
        User seller=userRepository.save(User.builder()
                .email("seller@seller")
                .nickname("seller")
                .password("password")
                .build());
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("buyer@email"));

        makeSuccessDealPost(buyer,seller);
        MannerReview mannerReview=mannerReviewRepository.save(MannerReview.builder()
                .buyer(buyer)
                .seller(seller)
                .manner(Manner.GOOD_KIND).build());
        String url="http://localhost:"+port+"/api/v1/manner-reviews/"+mannerReview.getId();
        // when
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        // then
        assertThat(mannerReviewRepository.findById(mannerReview.getId()).isPresent()).isFalse();
    }
}
