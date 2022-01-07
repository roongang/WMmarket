package com.around.wmmarket.service.dealReview;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealReview.DealReviewGetResponseDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_review.DealReviewRepository;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DealReviewService {
    // repo
    private final DealReviewRepository dealReviewRepository;
    // service
    private final UserService userService;

    public void save(String buyerEmail, String content, DealPost dealPost) {
        User buyer=userService.getUser(buyerEmail);
        User seller=dealPost.getUser();

        dealReviewRepository.save(DealReview.builder()
                .buyer(buyer)
                .seller(seller)
                .content(content)
                .dealPost(dealPost).build());

    }

    public DealReviewGetResponseDto getResponseDto(Integer dealReviewId){
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        DealReviewGetResponseDto responseDto=DealReviewGetResponseDto.builder()
                .content(dealReview.getContent())
                .createdDate(dealReview.getCreatedDate())
                .modifiedDate(dealReview.getModifiedDate())
                .dealPostId(dealReview.getDealPost().getId())
                .build();
        if(dealReview.getBuyer()!=null) responseDto.setBuyerId(dealReview.getBuyer().getId());
        if(dealReview.getSeller()!=null) responseDto.setSellerId(dealReview.getSeller().getId());
        return responseDto;
    }

    public void update(Integer dealReviewId,DealReviewUpdateRequestDto requestDto){
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        dealReview.setContent(requestDto.getContent());
    }
    public void delete(Integer dealReviewId) {
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        dealReview.deleteRelation();
        dealReviewRepository.delete(dealReview);
    }
}
