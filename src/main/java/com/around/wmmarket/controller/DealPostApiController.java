package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.List;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@Slf4j
@RequiredArgsConstructor
@RestController
public class DealPostApiController {
    private final DealPostService dealPostService;

    @PostMapping("/api/v1/test")
    public ResponseEntity<?> test(@ModelAttribute DealPostSaveRequestDto requestDto) throws Exception {
        log.info(requestDto.getTitle());
        if(!requestDto.getMultipartFiles().isEmpty()) log.info("multifile not empty");
        return ResponseEntity.ok("test success");
    }

    @PostMapping("/api/v1/dealPost")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser,@ModelAttribute DealPostSaveRequestDto requestDto) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("no user");
        dealPostService.save(signedUser,requestDto);
        return ResponseEntity.ok().body("save success");
    }
}
