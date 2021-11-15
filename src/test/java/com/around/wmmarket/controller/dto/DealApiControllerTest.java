package com.around.wmmarket.controller.dto;


import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
//@WebMvcTest는 JPA를 지원하지 않아서, JPA테스트시엔 SpringBootTest를 사용하면 된다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DealPostRepository postsRepository;

    @Autowired
    private UserRepository userRepository;

    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Transactional
    @Test
    public void DealPost_등록() throws Exception{
        //given
        Integer user = 1;
        Category category = Category.CategoryA;
        String title = "Test Title";
        Integer price = 9999;
        String content = "Test Content";
        DealState dealState = DealState.ING;
        postsRepository.save(DealPost.builder()
                        .category(category)
                        .title(title)
                        .price(price)
                        .content(content)
                        .dealState(dealState)
                        .build());
        //when
        List<DealPost> postList = postsRepository.findAll();

        //then
        DealPost posts= postList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    @Test
    public void DealPost_API등록() throws Exception{
        //given
        Integer user = 1;
        String category = "CategoryA";
        String title = "Test Title";
        Integer price = 9999;
        String content = "Test Content";
        String dealState = "ING";
//        userRepository.save(User.builder()
//                .email("test@admin")
//                .password("test")
//                .nickname("test")
//                .role(Role.USER)
//                .city_1("서울")
//                .town_1("동대문구")
//                .city_2("서울")
//                .town_2("서대문구")
//                .code("뭐였드라")
//                .build());

        DealPostSaveRequestDto requestDto = DealPostSaveRequestDto.builder()
                .user(user)
                .category(category)
                .title(title)
                .price(price)
                .content(content)
                .dealState(dealState)
                .build();

        String url = "http://localhost:"+port+"/api/v1/dealpost";
        //when
        ResponseEntity<Integer> responseEntity = restTemplate.postForEntity(url,requestDto,Integer.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0);

        List<DealPost> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

}
