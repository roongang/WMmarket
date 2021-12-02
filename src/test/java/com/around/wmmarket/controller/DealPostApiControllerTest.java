package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.UserSaveRequestDto;
import com.around.wmmarket.controller.dto.UserSigninRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.user.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealPostApiControllerTest {
    @LocalServerPort int port;

    @Autowired
    private WebApplicationContext context;

    private MockHttpSession session;
    private MockMvc mvc;

    @Before
    public void setup(){
        mvc= MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        session= new MockHttpSession();
    }

    @Test
    @WithMockUser
    public void DealPostSave() throws Exception{
        // given
        // multipart/form-data 형태이므로 setter 적용
        // 절대경로
        MockMultipartFile file1= new MockMultipartFile("img1","img1.jpg","image/jpeg","img1".getBytes());
        MockMultipartFile file2= new MockMultipartFile("img2","img2.png","image/png","img2".getBytes());

        DealPostSaveRequestDto requestDto = new DealPostSaveRequestDto();
        requestDto.setCategory(Category.A);
        requestDto.setTitle("title");
        requestDto.setPrice(1000);
        requestDto.setContent("content");
        requestDto.setMultipartFiles(new ArrayList<>());
        requestDto.getMultipartFiles().add(file1);
        requestDto.getMultipartFiles().add(file2);
        String url = "http://localhost:"+port+"/api/v1/dealPost";
        // when
        mvc.perform(multipart(url)
                .file(file1)
                .file(file2)
                .session(session)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("category",requestDto.getCategory().name())
                .param("title",requestDto.getTitle())
                .param("price",requestDto.getPrice().toString())
                .param("content",requestDto.getContent())
        ).andExpect(status().isOk());
        // then
    }
}
