package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewGetResponseDto;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.mannerReview.MannerReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class MannerReviewApiController {
    private final MannerReviewService mannerReviewService;
    // save
    @ApiOperation(value = "매너 리뷰 삽입")
    @PostMapping("/manner-reviews")
    public ResponseEntity<Object> save(@AuthenticationPrincipal SignedUser signedUser,
                                       @RequestBody MannerReviewSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("매너 리뷰 삽입 성공했습니다.")
                .data(mannerReviewService.save(signedUser,requestDto))
                .build());
    }

    @ApiOperation(value = "매너 리뷰 반환")
    @GetMapping("/manner-reviews/{mannerReviewId}")
    public ResponseEntity<Object> get(@PathVariable Integer mannerReviewId){
        MannerReviewGetResponseDto responseDto=mannerReviewService.get(mannerReviewId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("매너 리뷰 반환 성공했습니다.")
                .data(responseDto!=null
                        ? responseDto
                        : Collections.emptyList())
                .build());
    }

    @ApiOperation(value = "매너 리뷰 삭제")
    @DeleteMapping("/manner-reviews/{mannerReviewId}")
    public ResponseEntity<Object> delete(@AuthenticationPrincipal SignedUser signedUser,
                                         @PathVariable Integer mannerReviewId){
        mannerReviewService.delete(signedUser,mannerReviewId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("매너 리뷰 삭제 성공했습니다.")
                .build());
    }
}