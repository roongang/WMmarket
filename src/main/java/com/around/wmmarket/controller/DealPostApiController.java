package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@Validated
@RequestMapping(Constants.API_PATH)
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
    @PostMapping("/deal-posts")
    public ResponseEntity<?> save(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                  @Valid @ModelAttribute DealPostSaveRequestDto requestDto) {
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        dealPostService.save(userService.getUser(signedUser.getUsername()).getId(),requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("거래글 삽입 성공했습니다.").build());
    }

    @ApiOperation(value = "거래 글 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : dealPost info",response = DealPostGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/deal-posts/{dealPostId}")
    public ResponseEntity<?> get(
            @Min(1) @PathVariable("dealPostId") Integer dealPostId) {
        DealPostGetResponseDto responseDto=dealPostService.getDealPostGetResponseDto(dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(responseDto).build());
    }

    @ApiOperation(value = "거래 글 이미지 리스트 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : List dealPostImageId",response = ArrayList.class),
    }) // SWAGGER
    @GetMapping("/deal-posts/{dealPostId}/images")
    public ResponseEntity<?> getImages(
            @Min(1) @PathVariable("dealPostId") Integer dealPostId) {
        List<Integer> imageIds=dealPostService.getImages(dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래 글 이미지 리스트 반환 성공했습니다.")
                .data(imageIds).build());
    }

    @ApiOperation(value = "거래 글 수정") // SWAGGER
    @PutMapping("/deal-posts/{dealPostId}")
    public ResponseEntity<?> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealPostId") Integer dealPostId,
                                    @Valid @RequestBody DealPostUpdateRequestDto requestDto){
        // TODO : 너무너무 더럽다 다시 정리해야할듯!
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);

        DealPost dealPost=dealPostService.getDealPost(dealPostId);
        if(dealPost.getUser()==null||!dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        if(requestDto.getBuyerId()!=null&&!userService.isExist(requestDto.getBuyerId())) throw new CustomException(ErrorCode.BUYER_NOT_FOUND);
        if(requestDto.getBuyerId()!=null
                && userService.getUserEmail(requestDto.getBuyerId()).equals(signedUser.getUsername())) throw new CustomException(ErrorCode.SAME_BUYER_SELLER);

        dealPostService.update(dealPostId,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 삭제") // SWAGGER
    @DeleteMapping("/deal-posts/{dealPostId}")
    public ResponseEntity<?> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealPostId") Integer dealPostId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);

        DealPost dealPost=dealPostService.getDealPost(dealPostId);
        if(dealPost.getUser()==null||!dealPost.getUser().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("게시글 작성자가 아닙니다.");

        dealPostService.delete(dealPost);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 삭제 성공했습니다.")
                .build());
    }
}
