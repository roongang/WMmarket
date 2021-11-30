package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@RequiredArgsConstructor
@RestController
public class DealPostApiController {
    private final DealPostService dealPostService;

    @GetMapping("/api/v1/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal SignedUser signedUser){
        if(signedUser==null) return ResponseEntity.badRequest().body("no user");
        return ResponseEntity.ok(signedUser);
    }

    @PostMapping("/api/v1/dealPost")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser,@RequestBody DealPostSaveRequestDto requestDto){
        if(signedUser==null) return ResponseEntity.badRequest().body("no user");
        dealPostService.save(signedUser,requestDto);
        return ResponseEntity.ok().body("save success");
    }
}
