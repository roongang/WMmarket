package com.around.wmmarket.controller;

import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.dealReview.DealReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_review.DealReviewRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.dealReview.DealReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
public class DealReviewApiController {
    private final DealPostService dealPostService;
    private final DealReviewService dealReviewService;
    private final DealReviewRepository dealReviewRepository;

    @PostMapping("/api/v1/dealReview")
    public ResponseEntity<?> save(@AuthenticationPrincipal SignedUser signedUser, @RequestBody DealReviewSaveRequestDto requestDto) throws Exception{
        // check signedUser
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        DealPost dealPost=dealPostService.getDealPost(requestDto.getDealPostId());
        if(dealPost.getUser()==null||signedUser.getUsername().equals(dealPost.getUser().getEmail())) return ResponseEntity.badRequest().body("판매자는 본인글의 후기를 남길 수 없습니다.");
        // check dealState && dealSuccess
        if(dealPost.getDealState()!=DealState.DONE) return ResponseEntity.badRequest().body("게시글 상태가 DONE 이 아닙니다. 거래상태코드:"+dealPost.getDealState());
        if(dealPost.getDealSuccess().getBuyer()==null||!dealPost.getDealSuccess().getBuyer().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("거래글의 구매자와 로그인 유저가 일치하지 않습니다.");

        dealReviewService.save(signedUser.getUsername(),requestDto.getContent(),dealPost);

        return ResponseHandler.toResponse(SuccessResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("거래글 리뷰 삽입 성공했습니다.")
                .build());
    }

    @GetMapping("/api/v1/dealReview")
    public ResponseEntity<?> get(@RequestParam Integer dealReviewId) throws Exception{
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("거래글 리뷰 반환 성공했습니다.")
                .data(dealReviewService.getResponseDto(dealReviewId)).build());
    }

    @PutMapping("/api/v1/dealReview")
    public ResponseEntity<?> update(@AuthenticationPrincipal SignedUser signedUser,@RequestBody DealReviewUpdateRequestDto requestDto){
        // check signedUser
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        DealReview dealReview=dealReviewRepository.findById(requestDto.getDealReviewId())
                .orElseThrow(()->new NoSuchElementException("해당 리뷰글이 없습니다. reviewId:"+requestDto.getDealReviewId()));
        if(dealReview.getBuyer()==null||!dealReview.getBuyer().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("리뷰 작성자가 아닙니다.");
        // update
        dealReviewService.update(requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("거래글 리뷰 수정 성공했습니다.")
                .build());
    }

    @DeleteMapping("/api/v1/dealReview")
    public ResponseEntity<?> delete(@AuthenticationPrincipal SignedUser signedUser,@RequestParam Integer dealReviewId) throws Exception{
        // check signedUser
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()->new NoSuchElementException("해당 리뷰글이 없습니다. reviewId:"+dealReviewId));
        if(dealReview.getBuyer()==null||!dealReview.getBuyer().getEmail().equals(signedUser.getUsername())) return ResponseEntity.badRequest().body("리뷰 작성자가 아닙니다.");
        // delete
        dealReviewService.delete(dealReviewId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("거래글 리뷰 삭제 성공했습니다.")
                .build());
     }
}
