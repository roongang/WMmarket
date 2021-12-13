package com.around.wmmarket.service.dealReview;

import com.around.wmmarket.controller.dto.DealReview.DealReviewGetResponseDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_review.DealReviewRepository;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class DealReviewService {
    // repo
    private final DealReviewRepository dealReviewRepository;
    // service
    private final UserService userService;

    public void save(String buyerEmail, String content, DealPost dealPost) {
        User buyer=userService.getUser(buyerEmail);


        dealReviewRepository.save(DealReview.builder()
                .buyer(buyer)
                .content(content)
                .dealPost(dealPost).build());
    }

    public DealReviewGetResponseDto getResponseDto(Integer dealReviewId){
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()->new NoSuchElementException("해당 리뷰글이 없습니다. dealReviewId:"+dealReviewId));
        return DealReviewGetResponseDto.builder()
                .sellerId(dealReview.getDealPost().getUser().getId())
                .buyerId(dealReview.getBuyer().getId())
                .content(dealReview.getContent())
                .createdDate(dealReview.getCreatedDate())
                .modifiedDate(dealReview.getModifiedDate())
                .dealPostId(dealReview.getDealPost().getId())
                .build();
    }
}
