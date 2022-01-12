package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.user.UserSignInRequestDto;
import com.around.wmmarket.controller.dto.user.UserUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.domain.user_like.UserLikeRepository;
import com.around.wmmarket.common.FileHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private UserLikeRepository userLikeRepository;
    @Autowired
    private FileHandler fileHandler;

    @Autowired
    private WebApplicationContext context;

    private MockHttpSession session;
    private MockMvc mvc;
    private User user;
    private String image;

    @BeforeTransaction
    public void makeUser(){
        if(userRepository.existsByEmail("user@email")) return;
        user = User.builder()
                .email("user@email")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname")
                .role(Role.USER).build();
        image=fileHandler.parseUserImage(new MockMultipartFile("image","img.jpg","image/jpeg","img".getBytes(StandardCharsets.UTF_8)));
        user.setImage(image);
        userRepository.save(user);
        userRepository.save(User.builder()
                .email("deleteUser@email")
                .password(passwordEncoder.encode("password"))
                .nickname("nickname")
                .role(Role.USER).build());
    }
    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
        session = new MockHttpSession();
    }

    @AfterTransaction
    public void tearDown(){
        userRepository.deleteAll();
    }

    /////////////////////////////////////////////////////////////////////////// TEST
    @Test
    public void userSaveTest() throws Exception{
        // given
        String testEmail="test_email3@email";
        String testPassword="test_password";
        String testNickname="test_nickname";
        Role testRole=Role.USER;
        MockMultipartFile image= new MockMultipartFile("image","img.jpg","image/jpeg","img".getBytes(StandardCharsets.UTF_8));
        String url = "http://localhost:"+port+"/api/v1/users";
        // when
        mvc.perform(multipart(url)
                .file(image)
                .param("email",testEmail)
                .param("password",testPassword)
                .param("nickname",testNickname)
                .param("role",testRole.toString())
        ).andExpect(status().isCreated());
        // then
        List<User> allUser = userRepository.findAll();
        assertThat(allUser.get(0).getEmail()).isEqualTo(testEmail);
        log.info("user password = "+allUser.get(0).getPassword());
        assertThat(allUser.get(0).getNickname()).isEqualTo(testNickname);
        assertThat(allUser.get(0).getRole()).isEqualTo(testRole);
    }

    @Test
    public void userSignInTest() throws Exception{
        // given
        String testEmail="test_email2@email";
        String testPassword="test_password2";
        String testNickname="test_nickname2";
        Role testRole=Role.USER;
        userRepository.save(User.builder()
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .nickname(testNickname)
                .role(testRole)
                .build());

        UserSignInRequestDto requestDto = UserSignInRequestDto.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
        String url = "http://localhost:"+port+"/api/v1/signin";
        // when
        mvc.perform(post(url)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isCreated());
        // then
        Object object = session.getAttribute("SPRING_SECURITY_CONTEXT");
        SecurityContextImpl context = (SecurityContextImpl) object;
        SignedUser signedUser = (SignedUser) context.getAuthentication().getPrincipal();
        assertThat(testEmail).isEqualTo(signedUser.getUsername());
        assertThat(passwordEncoder.matches(testPassword,signedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    public void userGetTest() throws Exception{
        // given
        String url="http://localhost:"+port+"/api/v1/users";
        // when
        MvcResult result=mvc.perform(get(url)
                .param("email","user@email"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains("user@email");
        assertThat(result.getResponse().getContentAsString()).contains("nickname");
        assertThat(result.getResponse().getContentAsString()).contains("USER");
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void userUpdateTest() throws Exception{
        // given
        UserUpdateRequestDto requestDto= UserUpdateRequestDto.builder()
                .password("update_password")
                .nickname("update_nickname")
                .build();
        User user=userRepository.findByEmail("user@email")
                .orElseThrow(() -> new UsernameNotFoundException("user@email"));
        String url="http://localhost:"+port+"/api/v1/users/"+user.getId();
        // when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto))
        ).andExpect(status().isOk());
        // then
        User updateUser=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user:user@email not found"));
        assertThat(passwordEncoder.matches("update_password", updateUser.getPassword())).isTrue();
        assertThat(updateUser.getNickname()).isEqualTo("update_nickname");
    }

    @Test
    @Transactional
    @WithUserDetails(value = "deleteUser@email")
    public void userDeleteTest() throws Exception{
        // given
        User deleteUser=userRepository.findByEmail("deleteUser@email")
                .orElseThrow(()->new UsernameNotFoundException("deleteUser"));
        String url="http://localhost:"+port+"/api/v1/users/"+deleteUser.getId();
        // when
        mvc.perform(delete(url)
                .session(session)
        ).andExpect(status().isOk());
        // then
        assertThat(userRepository.findByEmail("deleteUser@email")).isEmpty();
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    @Transactional
    public void userImageGetTest() throws Exception{
        // given
        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/image";
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION)).contains(image);
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void userImageUpdateTest() throws Exception{
        // given
        MultipartFile file=new MockMultipartFile("image","img.jpg","image/jpeg","img".getBytes(StandardCharsets.UTF_8));
        String image=fileHandler.parseUserImage(file);
        user.setImage(image);

        MockMultipartFile updateFile=new MockMultipartFile("file","img.jpg","image/jpeg","updateImg".getBytes(StandardCharsets.UTF_8));

        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        String url="http://localhost"+port+"/api/v1/users/"+user.getId()+"/image";
        // when
        MockMultipartHttpServletRequestBuilder builder=multipart(url);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mvc.perform(builder
                .file(updateFile))
                .andExpect(status().isOk());
        // then
        assertThat(userRepository.findAll().get(0).getImage()).isNotEqualTo(image);
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void userImageDeleteTest() throws Exception{
        // given
        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        String url="http://localhost"+port+"/api/v1/users/"+user.getId()+"/image";
        // when
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        // then
        User testUser=userRepository.findByEmail("user@email")
                        .orElseThrow(()->new UsernameNotFoundException("user@email not found"));
        assertThat(testUser.getImage()).isNull();
    }

    // userLike
    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void userLikeSaveTest() throws Exception{
        // given
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.ONGOING).build());
        DealPost dealPost=dealPostRepository.findAll().get(0);

        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));

        String url="http://localhost"+port+"/api/v1/users/"+user.getId()+"/likes";
        // when
        mvc.perform(post(url)
                .param("dealPostId",dealPost.getId().toString()))
                .andExpect(status().isCreated());
        // then
        user=userRepository.findByEmail("user@email")
                        .orElseThrow(()->new UsernameNotFoundException("user@email"));
        log.info("likes num:"+user.getUserLikes().size());
        assertThat(userLikeRepository.findAll().isEmpty()).isFalse();
    }

    @Test
    @Transactional
    public void userLikesGetTest() throws Exception{
        // given
        // save dealPost & like
        user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.ONGOING).build());
        DealPost dealPost=dealPostRepository.findAll().get(0);

        userLikeRepository.save(UserLike.builder()
                .user(user)
                .dealPost(dealPost)
                .build());

        String url="http://localhost"+port+"/api/v1/users/"+user.getId()+"/likes";
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        log.info("likes num:"+user.getUserLikes().size());
        log.info("result:"+result.getResponse().getContentAsString());
        assertThat(result.getResponse().getContentAsString()).contains(dealPost.getId().toString());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void userLikesDeleteTest() throws Exception{
        // given
        // save dealPost & like
        user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        dealPostRepository.save(DealPost.builder()
                .user(user)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.ONGOING).build());
        DealPost dealPost=dealPostRepository.findAll().get(0);

        userLikeRepository.save(UserLike.builder()
                .user(user)
                .dealPost(dealPost)
                .build());

        String url="http://localhost"+port+"/api/v1/users/"+user.getId()+"/likes";
        // when
        mvc.perform(delete(url)
                        .param("dealPostId",dealPost.getId().toString()))
                .andExpect(status().isOk());
        // then
        user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        log.info("userLike size:"+user.getUserLikes().size());
        assertThat(user.getUserLikes().isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user@email")
    public void userDealPostsGetTest() throws Exception{
        // given
        // 글 여러개 쓰기
        for(int i=1;i<=5;i++){
            dealPostRepository.save(DealPost.builder()
                    .user(user)
                    .category(Category.valueOf(Category.A.name()))
                    .title("title")
                    .price(i*1000)
                    .content("content")
                    .dealState(DealState.ONGOING).build());
        }
        String url="http://localhost"+port+"/api/v1/users/"+user.getId()+"/deal-posts";
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        for(int i=1;i<=5;i++){
            String price=Integer.toString(i*1000);
            assertThat(result.getResponse().getContentAsString()).contains(price);
        }
    }
}
