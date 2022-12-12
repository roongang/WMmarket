package com.around.wmmarket.controller;

import com.around.wmmarket.config.WithAccount;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.user.UserLikeDeleteRequestDto;
import com.around.wmmarket.controller.dto.user.UserLikeSaveRequestDto;
import com.around.wmmarket.controller.dto.user.UserSignInRequestDto;
import com.around.wmmarket.controller.dto.user.UserUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.deal_success.DealSuccessRepository;
import com.around.wmmarket.domain.manner_review.Manner;
import com.around.wmmarket.domain.manner_review.MannerReview;
import com.around.wmmarket.domain.manner_review.MannerReviewRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.domain.user_like.UserLikeId;
import com.around.wmmarket.domain.user_like.UserLikeRepository;
import com.around.wmmarket.domain.user_role.Role;
import com.around.wmmarket.domain.user_role.UserRole;
import com.around.wmmarket.domain.user_role.UserRoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealPostRepository dealPostRepository;
    @Autowired
    private UserLikeRepository userLikeRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private DealSuccessRepository dealSuccessRepository;
    @Autowired
    private MannerReviewRepository mannerReviewRepository;

    private MockHttpSession session;
    private MockMvc mvc;

    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
        session = new MockHttpSession();
    }

    public void makeSuccessDealPost(User buyer,User seller) {
        DealPost dealPost=dealPostRepository.save(DealPost.builder()
                .user(seller)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.DONE).build());

        dealSuccessRepository.save(DealSuccess.builder()
                .buyer(buyer)
                .dealPost(dealPost)
                .build());
    }
    /////////////////////////////////////////////////////////////////////////// TEST
    @Test
    public void userSaveTest() throws Exception{
        // given
        String testEmail="test_email@email";
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
                .param("roles",testRole.toString())
        ).andExpect(status().isCreated());
        // then
        User testUser=userRepository.findByEmail(testEmail)
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
        assertThat(testUser.getEmail()).isEqualTo(testEmail);
        log.info("user password = "+testUser.getPassword());
        assertThat(testUser.getNickname()).isEqualTo(testNickname);
        assertThat(testUser.getUserRoles().get(0).getRole()).isEqualTo(testRole);

        for(User tmp:userRepository.findAll()){
            log.info("user.getId() = "+tmp.getId());
            log.info("user.getEmail() = "+tmp.getEmail());
        }
    }

    @Test
    public void userSignInTest() throws Exception{
        // given
        String testEmail="test_email@email";
        String testPassword="test_password";
        String testNickname="test_nickname";
        userRepository.save(User.builder()
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .nickname(testNickname)
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
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        SignedUser signedUser = (SignedUser) securityContext.getAuthentication().getPrincipal();
        assertThat(testEmail).isEqualTo(signedUser.getUsername());
        assertThat(passwordEncoder.matches(testPassword,signedUser.getPassword())).isTrue();
    }

    @Test
    public void userGetTest() throws Exception{
        // given
        String testEmail="test_email@email";
        String testPassword="test_password";
        String testNickname="test_nickname";
        User user=userRepository.save(User.builder()
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .nickname(testNickname)
                .build());
        userRoleRepository.save(UserRole.builder()
                .user(user)
                .role(Role.USER)
                .build());

        String url="http://localhost:"+port+"/api/v1/users";
        // when
        MvcResult result=mvc.perform(get(url)
                .param("email",testEmail))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains(testEmail);
        assertThat(result.getResponse().getContentAsString()).contains(testNickname);
        assertThat(result.getResponse().getContentAsString()).contains("USER");
    }

    @Test
    @WithAccount(email="user@email")
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
    @WithAccount(email="admin@email",roles = {Role.ADMIN})
    public void userUpdateByAdminTest() throws Exception {
        // given
        UserUpdateRequestDto requestDto= UserUpdateRequestDto.builder()
                .password("update password")
                .nickname("update nickname")
                .build();
        User user=userRepository.save(User.builder()
                .email("user@email")
                .password(passwordEncoder.encode("user_password"))
                .nickname("user nickname")
                .build());

        String url="http://localhost:"+port+"/api/v1/users/"+user.getId();
        // when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto))
        ).andExpect(status().isOk());
        // then
        User updateUser=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email not found"));
        assertThat(passwordEncoder.matches("update password", updateUser.getPassword())).isTrue();
        assertThat(updateUser.getNickname()).isEqualTo("update nickname");
    }

    @Test
    @WithAccount(email="deleteUser@email")
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
    @WithAccount(email="admin@email",roles = {Role.ADMIN})
    public void userDeleteByAdminTest() throws Exception{
        // given
        User deleteUser = userRepository.save(User.builder()
                .email("deleteUser@email")
                .password("password")
                .nickname("deleteUser nickname")
                .build());

        String url="http://localhost:"+port+"/api/v1/users/"+deleteUser.getId();
        // when
        mvc.perform(delete(url)
                .session(session)
        ).andExpect(status().isOk());
        // then
        assertThat(userRepository.findByEmail(deleteUser.getEmail())).isEmpty();
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    @WithAccount(email="user@email")
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
        assertThat(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION)).contains(user.getImage());
    }

    @Test
    @WithAccount(email="user@email")
    public void userImageUpdateTest() throws Exception{
        // given
        MockMultipartFile updateFile=new MockMultipartFile("file","img.jpg","image/jpeg","updateImg".getBytes(StandardCharsets.UTF_8));

        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        String priorImage=user.getImage();

        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/image";
        // when
        MockMultipartHttpServletRequestBuilder builder=multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        mvc.perform(builder
                .file(updateFile))
                .andExpect(status().isOk());
        // then
        assertThat(userRepository.findAll().get(0).getImage()).isNotEqualTo(priorImage);
    }

    @Test
    @WithAccount(email="user@email")
    public void userImageDeleteTest() throws Exception{
        // given
        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/image";
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
    @WithAccount(email="user@email")
    public void userLikeSaveTest() throws Exception{
        // given
        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        DealPost dealPost=dealPostRepository.save(DealPost.builder()
                .user(user)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.ONGOING).build());

        UserLikeSaveRequestDto requestDto=new UserLikeSaveRequestDto();
        requestDto.setDealPostId(dealPost.getId());

        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/likes";
        // when
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
        // then
        assertThat(userLikeRepository.findById(UserLikeId.builder()
                .userId(user.getId())
                .dealPostId(dealPost.getId())
                .build())).isNotNull();
    }

    @Test
    public void userLikesGetTest() throws Exception{
        // given
        // save dealPost & like
        User user = userRepository.save(User.builder()
                .email("user@email")
                .password("password")
                .nickname("user nickname")
                .build());
        DealPost dealPost=dealPostRepository.save(DealPost.builder()
                .user(user)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.ONGOING).build());

        userLikeRepository.save(UserLike.builder()
                .user(user)
                .dealPost(dealPost)
                .build());

        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/likes";
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains(dealPost.getId().toString());
        assertThat(userLikeRepository.findById(UserLikeId.builder()
                .userId(user.getId())
                .dealPostId(dealPost.getId())
                .build())).isNotNull();
    }

    @Test
    @WithAccount(email="user@email")
    public void userLikesDeleteTest() throws Exception{
        // given
        // save dealPost & like
        User user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        DealPost dealPost=dealPostRepository.save(DealPost.builder()
                .user(user)
                .price(1000)
                .content("content")
                .title("title")
                .category(Category.A)
                .dealState(DealState.ONGOING).build());

        user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        userLikeRepository.save(UserLike.builder()
               .user(user)
               .dealPost(dealPost)
               .build());

        UserLikeDeleteRequestDto requestDto=new UserLikeDeleteRequestDto();
        requestDto.setDealPostId(dealPost.getId());

        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/likes";
        // when
        mvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        // then
        user=userRepository.findByEmail("user@email")
                .orElseThrow(()->new UsernameNotFoundException("user@email"));
        log.info("userLike size:"+user.getUserLikes().size());
        assertThat(user.getUserLikes().isEmpty()).isTrue();
    }

    @Test
    public void userDealPostsGetTest() throws Exception{
        // given
        User user=userRepository.save(User.builder()
                .email("user@email")
                .password("password")
                .nickname("user nickname").build());
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
        String url="http://localhost:"+port+"/api/v1/users/"+user.getId()+"/deal-posts";
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

    @Test
    @WithAccount(email="buyer@email")
    public void userMannerReviewSaveTest() throws Exception{
        // given
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("buyer@email"));
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .password("password")
                .nickname("seller nickname").build());

        makeSuccessDealPost(buyer,seller);

        MannerReviewSaveRequestDto requestDto=MannerReviewSaveRequestDto.builder()
                .sellerId(seller.getId())
                .buyerId(buyer.getId())
                .manner("GOOD_KIND").build();
        String url="http://localhost:"+port+"/api/v1/users/"+buyer.getId()+"/buy-manner-reviews";
        // when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
        // then
        Assertions.assertThat(mannerReviewRepository.findAll()).isNotNull();
    }

    @Test
    public void userSellMannerReviewGetTest() throws Exception{
        // given
        User buyer=userRepository.save(User.builder()
                .email("buyer@email")
                .password("password")
                .nickname("buyer nickname").build());
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .password("password")
                .nickname("seller nickname").build());

        makeSuccessDealPost(buyer,seller);

        mannerReviewRepository.save(MannerReview.builder()
                .buyer(buyer)
                .seller(seller)
                .manner(Manner.GOOD_KIND).build());
        String url="http://localhost:"+port+"/api/v1/users/"+seller.getId()+"/sell-manner-reviews";
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains("sellerId");
        assertThat(result.getResponse().getContentAsString()).contains("buyerId");
    }

    @Test
    public void userBuyMannerReviewGetTest() throws Exception{
        // given
        User buyer=userRepository.save(User.builder()
                .email("buyer@email")
                .password("password")
                .nickname("buyer nickname").build());
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .password("password")
                .nickname("seller nickname").build());

        makeSuccessDealPost(buyer,seller);
        mannerReviewRepository.save(MannerReview.builder()
                .buyer(buyer)
                .seller(seller)
                .manner(Manner.GOOD_KIND).build());
        String url="http://localhost:"+port+"/api/v1/users/"+buyer.getId()+"/buy-manner-reviews";
        // when
        MvcResult result=mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        // then
        assertThat(result.getResponse().getContentAsString()).contains("sellerId");
        assertThat(result.getResponse().getContentAsString()).contains("buyerId");
    }

    @Test
    @WithAccount(email="buyer@email")
    public void userBuyMannerReviewDeleteTest() throws Exception{
        // given
        User buyer=userRepository.findByEmail("buyer@email")
                .orElseThrow(()->new UsernameNotFoundException("buyer@email"));
        User seller=userRepository.save(User.builder()
                .email("seller@email")
                .password("password")
                .nickname("seller nickname").build());

        makeSuccessDealPost(buyer,seller);
        MannerReview mannerReview=mannerReviewRepository.save(MannerReview.builder()
                .buyer(buyer)
                .seller(seller)
                .manner(Manner.GOOD_KIND).build());
        Integer mannerReviewId=mannerReview.getId();
        String url="http://localhost:"+port+"/api/v1/users/"+buyer.getId()+"/buy-manner-reviews/"+mannerReviewId;
        // when
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        // then
        assertThat(mannerReviewRepository.findById(mannerReviewId).isPresent()).isFalse();
    }
}
