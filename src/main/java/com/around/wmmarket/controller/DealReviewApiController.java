package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealReview.DealReviewGetResponseDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_review.DealReviewRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPost.DealPostService;
import com.around.wmmarket.service.dealReview.DealReviewService;
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

@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class DealReviewApiController {
    private final DealPostService dealPostService;
    private final DealReviewService dealReviewService;
    private final DealReviewRepository dealReviewRepository;

    @ApiOperation(value = "거래 리뷰 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/deal-reviews")
    public ResponseEntity<?> save(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                  @Valid @RequestBody DealReviewSaveRequestDto requestDto) {
        dealReviewService.save(signedUser,requestDto.getContent(),requestDto.getDealPostId());
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 리뷰 삽입 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 리뷰 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : dealReview info",response = DealReviewGetResponseDto.class)
    }) // SWAGGER
    @GetMapping("/deal-reviews/{dealReviewId}")
    public ResponseEntity<?> get(
            @Min(1) @PathVariable("dealReviewId") Integer dealReviewId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 리뷰 반환 성공했습니다.")
                .data(dealReviewService.getDealReviewDto(dealReviewId))
                .build());
    }

    @ApiOperation(value = "거래 글 리뷰 수정") // SWAGGER
    @PutMapping("/deal-reviews/{dealReviewId}")
    public ResponseEntity<?> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealReviewId") Integer dealReviewId,
                                    @Valid @RequestBody DealReviewUpdateRequestDto requestDto){
        // check signedUser
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()->new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        if(dealReview.getBuyer()==null||!dealReview.getBuyer().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEAL_REVIEW);
        // update
        dealReviewService.update(signedUser,dealReviewId,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 리뷰 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 리뷰 삭제") // SWAGGER
    @DeleteMapping("/deal-reviews/{dealReviewId}")
    public ResponseEntity<?> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealReviewId") Integer dealReviewId) {
        // check signedUser
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        if(dealReview.getBuyer()==null||!dealReview.getBuyer().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEAL_REVIEW);

        // delete
        dealReviewService.delete(dealReviewId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 리뷰 삭제 성공했습니다.")
                .build());
     }
}
