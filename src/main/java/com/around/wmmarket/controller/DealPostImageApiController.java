package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.dealPostImage.DealPostImageSaveRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.common.Constants;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DealPostImageApiController {
    private final DealPostImageService dealPostImageService;
    private final DealPostService dealPostService;
    private final ResourceLoader resourceLoader;
    private final Tika tika=new Tika();

    @Transactional
    @PostMapping("/api/v1/dealPostImage")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser, @ModelAttribute DealPostImageSaveRequestDto requestDto) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        // signedUser 와 dealPostId 의 email 비교
        if(!dealPostService.isDealPostAuthor(signedUser,requestDto.getDealPostId())){
            return ResponseEntity.badRequest().body("게시글의 작성자가 아닙니다.");
        }
        DealPost dealPost=dealPostService.getDealPost(requestDto.getDealPostId());
        dealPostImageService.save(dealPost,requestDto.getFiles());
        return ResponseEntity.ok().body("save success");
    }

    @Transactional
    @DeleteMapping("/api/v1/dealPostImage")
    public ResponseEntity<?> delete(@AuthenticationPrincipal SignedUser signedUser,@RequestParam Integer dealPostImageId) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        // signedUser 와 dealPostId 의 email 비교
        DealPostImage dealPostImage=dealPostImageService.get(dealPostImageId);
        if(!dealPostService.isDealPostAuthor(signedUser,dealPostImage.getDealPost().getId())){
            return ResponseEntity.badRequest().body("게시글의 작성자가 아닙니다.");
        }
        dealPostImageService.delete(dealPostImageId);
        return ResponseEntity.ok().body("delete success");
    }

    @GetMapping("/api/v1/dealPostImage")
    public ResponseEntity<?> get(@RequestParam Integer dealPostImageId) throws Exception{
        DealPostImage dealPostImage=dealPostImageService.get(dealPostImageId);
        String fileName=dealPostImage.getName();
        Resource resource=resourceLoader.getResource("file:"+ Paths.get(Constants.dealPostImagePath.toString(),fileName));
        File file=resource.getFile();
        String mediaType=tika.detect(file);

        HttpHeaders headers=new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+resource.getFilename()+"\"");
        headers.add(HttpHeaders.CONTENT_TYPE,mediaType);
        headers.add(HttpHeaders.CONTENT_LENGTH,String.valueOf(file.length()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

}
