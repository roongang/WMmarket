package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.List;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@RequiredArgsConstructor
@RestController
public class DealPostApiController {
    private final DealPostService dealPostService;

    @PostMapping("/api/v1/test")
    public ResponseEntity<?> test(@RequestPart List<MultipartFile> images) throws IOException {
        String rootPath= FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath= rootPath+"/"+"Image";
        for(MultipartFile image:images){
            String originalFilename=image.getOriginalFilename();
            File dest=new File(basePath+originalFilename);
            image.transferTo(dest);
        }
        return ResponseEntity.ok("image saved");
    }

    @PostMapping("/api/v1/dealPost")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser,@RequestBody DealPostSaveRequestDto requestDto,@RequestBody List<MultipartFile> multipartFiles) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("no user");
        dealPostService.save(signedUser,requestDto,multipartFiles);
        return ResponseEntity.ok().body("save success");
    }
}
