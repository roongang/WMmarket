package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSearchRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Collections;

// TODO : @AuthenticationPrincipal adapter 패턴으로 감싸야하는가 의문
@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class DealPostApiController {
    private final DealPostService dealPostService;

    @ApiOperation(value = "거래 글 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/deal-posts")
    public ResponseEntity<?> save(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                  @Valid @ModelAttribute DealPostSaveRequestDto requestDto) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("거래글 삽입 성공했습니다.")
                .data(dealPostService.save(signedUser,requestDto))
                .build());
    }

    @ApiOperation(value = "거래 글 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : dealPost info",response = DealPostGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/deal-posts/{dealPostId}")
    public ResponseEntity<?> get(
            @Min(1) @PathVariable("dealPostId") Integer dealPostId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 반환 성공하였습니다.")
                .data(dealPostService.getDealPostDto(dealPostId)!=null
                        ? dealPostService.getDealPostDto(dealPostId)
                        : Collections.emptyList()).build());
    }

    @ApiOperation(value = "거래 글 이미지 리스트 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : List dealPostImageId",response = ArrayList.class),
    }) // SWAGGER
    @GetMapping("/deal-posts/{dealPostId}/images")
    public ResponseEntity<?> getImages(
            @Min(1) @PathVariable("dealPostId") Integer dealPostId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래 글 이미지 리스트 반환 성공했습니다.")
                .data(dealPostService.getImages(dealPostId)).build());
    }

    @ApiOperation(value = "거래 글 수정") // SWAGGER
    @PutMapping("/deal-posts/{dealPostId}")
    public ResponseEntity<?> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealPostId") Integer dealPostId,
                                    @Valid @RequestBody DealPostUpdateRequestDto requestDto){
        dealPostService.update(signedUser,dealPostId,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래글 조회수 증가")
    @PutMapping("/deal-posts/{dealPostId}/view-cnt")
    public ResponseEntity<?> increaseViewCnt(@Min(1) @PathVariable("dealPostId") Integer dealPostId,
                                             @Valid @RequestBody DealPostUpdateRequestDto requestDto){
        dealPostService.increaseViewCnt(dealPostId,requestDto.getViewCnt());
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 조회수 증가 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 삭제") // SWAGGER
    @DeleteMapping("/deal-posts/{dealPostId}")
    public ResponseEntity<?> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealPostId") Integer dealPostId) {
        dealPostService.delete(signedUser,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 삭제 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 검색") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : slice",response = Slice.class),
    })
    @GetMapping("/deal-posts/page")
    public ResponseEntity<Object> searchDealPost(DealPostSearchRequestDto requestDto){
        // TODO : filter validation 은?
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 검색 성공했습니다.")
                .data(dealPostService.findByFilter(requestDto))
                .build());
    }
    // pulling
    @ApiOperation(value = "거래 글 끌어올리기") // SWAGGER
    @PutMapping("/deal-posts/{dealPostId}/pulling")
    public ResponseEntity<Object> pullDealPost(@AuthenticationPrincipal SignedUser signedUser,
                                                @PathVariable("dealPostId") Integer dealPostId){
        dealPostService.pull(signedUser,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래 글 끌어올리기 성공해습니다.")
                .build());
    }
}
