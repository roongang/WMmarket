package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.mannerReview.MannerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class MannerReviewApiController {
    private final MannerReviewService mannerReviewService;
    // save
    @PostMapping("/manner-reviews")
    public ResponseEntity<Object> save(@AuthenticationPrincipal SignedUser signedUser,
                                       @RequestBody MannerReviewSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("매너 리뷰 삽입 성공했습니다.")
                .data(mannerReviewService.save(signedUser,requestDto))
                .build());
    }
}