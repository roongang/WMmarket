package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.DealPost.DealPostSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@Slf4j
@RequiredArgsConstructor
@RestController
public class DealPostApiController {
    private final DealPostService dealPostService;

    @PostMapping("/api/v1/dealPost")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser,@ModelAttribute DealPostSaveRequestDto requestDto) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        dealPostService.save(signedUser,requestDto);
        return ResponseEntity.ok().body("save success");
    }

    @GetMapping("/api/v1/dealPost")
    public ResponseEntity<?> get(@RequestParam Integer dealPostId) throws Exception{
        DealPostGetResponseDto responseDto=dealPostService.getDealPostGetResponseDto(dealPostId);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/api/v1/dealPost/images")
    public ResponseEntity<?> getImages(@RequestParam Integer dealPostId){
        List<Integer> images=dealPostService.getImages(dealPostId);
        return ResponseEntity.ok().body(images);
    }
}
