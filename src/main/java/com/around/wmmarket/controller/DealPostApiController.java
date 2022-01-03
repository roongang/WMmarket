package com.around.wmmarket.controller;

import com.around.wmmarket.common.ResourceResponse;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

    @ApiOperation(value = "거래 글 삽입")
    @PostMapping("/api/v1/dealPost")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser,@ModelAttribute DealPostSaveRequestDto requestDto) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        dealPostService.save(signedUser,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 삽입 성공했습니다.").build());
    }

    @ApiOperation(value = "거래 글 반환")
    @GetMapping("/api/v1/dealPost")
    public ResponseEntity<?> get(@RequestParam Integer dealPostId) throws Exception{
        DealPostGetResponseDto responseDto=dealPostService.getDealPostGetResponseDto(dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(responseDto).build());
    }

    @ApiOperation(value = "거래 글 이미지 리스트 반환")
    @GetMapping("/api/v1/dealPost/images")
    public ResponseEntity<?> getImages(@RequestParam Integer dealPostId){
        List<Integer> images=dealPostService.getImages(dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 리스트 반환 성공했습니다.")
                .data(images).build());
    }

    @ApiOperation(value = "거래 글 수정")
    @PutMapping("/api/v1/dealPost")
    public ResponseEntity<?> update(@AuthenticationPrincipal SignedUser signedUser, @RequestBody DealPostUpdateRequestDto requestDto){
        // TODO : 너무너무 더럽다 다시 정리해야할듯!
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        DealPost dealPost=dealPostService.getDealPost(requestDto.getDealPostId());
        if(dealPost.getUser()==null||!dealPost.getUser().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("게시글 작성자가 아닙니다.");
        if(requestDto.getBuyerId()!=null&&!userService.isExist(requestDto.getBuyerId())) return ResponseEntity.badRequest().body("구매자가 존재하지 않습니다.");
        if(requestDto.getBuyerId()!=null
                && userService.getUserEmail(requestDto.getBuyerId()).equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("구매자와 판매자가 일치할 수 없습니다.");

        dealPostService.update(requestDto);
        DealPostGetResponseDto responseDto=dealPostService.getDealPostGetResponseDto(requestDto.getDealPostId());
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 수정 성공했습니다.")
                .data(responseDto).build());
    }

    @ApiOperation(value = "거래 글 삭제")
    @DeleteMapping("/api/v1/dealPost")
    public ResponseEntity<?> delete(@AuthenticationPrincipal SignedUser signedUser,@RequestParam Integer dealPostId) throws Exception{
        // check
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        DealPost dealPost=dealPostService.getDealPost(dealPostId);
        if(dealPost.getUser()==null||!dealPost.getUser().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("게시글 작성자가 아닙니다.");

        dealPostService.delete(dealPost);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 삭제 성공했습니다.")
                .build());
    }
}
