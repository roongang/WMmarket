package com.around.wmmarket.controller;

import com.around.wmmarket.common.ResourceResponse;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@Slf4j
@RequiredArgsConstructor
@RestController
public class DealPostApiController {
    private final DealPostService dealPostService;
    private final UserService userService;

    @ApiOperation(value = "거래 글 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/api/v1/dealPost")
    public ResponseEntity<?> save(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,@ModelAttribute DealPostSaveRequestDto requestDto) throws Exception{
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        dealPostService.save(signedUser,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 삽입 성공했습니다.").build());
    }

    @ApiOperation(value = "거래 글 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : dealPost info",response = DealPostGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/api/v1/dealPost")
    public ResponseEntity<?> get(
            @ApiParam(value = "거래 글 아이디",example = "1",required = true)
            @RequestParam Integer dealPostId) throws Exception{
        DealPostGetResponseDto responseDto=dealPostService.getDealPostGetResponseDto(dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(responseDto).build());
    }

    @ApiOperation(value = "거래 글 이미지 리스트 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : List dealPostImageId",response = ArrayList.class),
    }) // SWAGGER
    @GetMapping("/api/v1/dealPost/images")
    public ResponseEntity<?> getImages(
            @ApiParam(value = "거래 글 아이디",example = "1",required = true)
            @RequestParam Integer dealPostId){
        List<Integer> imageIds=dealPostService.getImages(dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래 글 이미지 리스트 반환 성공했습니다.")
                .data(imageIds).build());
    }

    @ApiOperation(value = "거래 글 수정") // SWAGGER
    @PutMapping("/api/v1/dealPost")
    public ResponseEntity<?> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser, @RequestBody DealPostUpdateRequestDto requestDto){
        // TODO : 너무너무 더럽다 다시 정리해야할듯!
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        DealPost dealPost=dealPostService.getDealPost(requestDto.getDealPostId());
        if(dealPost.getUser()==null||!dealPost.getUser().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("게시글 작성자가 아닙니다.");
        if(requestDto.getBuyerId()!=null&&!userService.isExist(requestDto.getBuyerId())) return ResponseEntity.badRequest().body("구매자가 존재하지 않습니다.");
        if(requestDto.getBuyerId()!=null
                && userService.getUserEmail(requestDto.getBuyerId()).equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("구매자와 판매자가 일치할 수 없습니다.");

        dealPostService.update(requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 삭제") // SWAGGER
    @DeleteMapping("/api/v1/dealPost")
    public ResponseEntity<?> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                    @RequestParam Integer dealPostId) throws Exception{
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
