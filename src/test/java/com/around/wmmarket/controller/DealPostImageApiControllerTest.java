package com.around.wmmarket.controller;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_post_image.DealPostImageRepository;
import com.around.wmmarket.domain.user_role.Role;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_role.UserRole;
import com.around.wmmarket.domain.user_role.UserRoleRepository;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private DealPostImageService dealPostImageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private DealPostImageRepository dealPostImageRepository;

    private MockMvc mvc;

    static User user;
    static DealPost dealPost;

    @BeforeTransaction
    public void makeUser(){
        //if(userRepository.existsByEmail("user@email")) return;
        user = User.builder()
                .email("user@email")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname")
                .build();
        userRepository.save(user);
        userRoleRepository.save(UserRole.builder()
                .user(user)
                .role(Role.USER)
                .build());
        dealPost=DealPost.builder()
                .user(user)
                .category(Category.A)
                .title("title")
                .price(1000)
                .content("content")
                .dealState(DealState.ONGOING).build();
        dealPostRepository.save(dealPost);
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
        // repo
        dealPostImageRepository.deleteAll();
        dealPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostImageSave() throws Exception{
        // given
        MockMultipartFile file1= new MockMultipartFile("files","img1.jpg","image/jpeg","img1".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2= new MockMultipartFile("files","img2.png","image/png","img2".getBytes(StandardCharsets.UTF_8));

        String url = "http://localhost:"+port+"/api/v1/deal-post-images";
        // when
        mvc.perform(multipart(url)
                .file(file1)
                .file(file2)
                .param("dealPostId",dealPost.getId().toString())
        ).andExpect(status().isCreated());
        // then
        dealPost=dealPostRepository.findAll().get(0);
        log.info("image list size:"+dealPost.getDealPostImages().size()); // TODO : 어떻게 리스트에 4개가 저장되는거지???? 도저히 모르겠네
        for(DealPostImage image:dealPost.getDealPostImages()){
            log.info(image.getName());
        }
        log.info("##################################");
        List<DealPostImage> list=dealPostImageRepository.findAll();
        for(DealPostImage image:list){
            log.info(image.getName());
        }
        // then
        // for(DealPostImage dealPostImage:dealPost.getDealPostImages()) dealPostImageService.delete(dealPostImage.getId());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void dealPostImageDeleteTest() throws Exception{
        // given
        List<MultipartFile> files=new ArrayList<>();
        files.add(new MockMultipartFile("files","img.jpg","image/jpeg","img".getBytes(StandardCharsets.UTF_8)));
        dealPostImageService.save(dealPost,files);
        int dealPostImageId=dealPostImageRepository.findAll().get(0).getId();

        String url = "http://localhost:"+port+"/api/v1/deal-post-images/"+dealPostImageId;
        // when
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        // then
        assertThat(dealPostImageRepository.findAll().isEmpty()).isTrue();
    }
}
