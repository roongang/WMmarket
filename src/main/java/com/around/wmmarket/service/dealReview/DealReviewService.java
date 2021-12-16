package com.around.wmmarket.service.dealReview;

import com.around.wmmarket.controller.dto.dealReview.DealReviewGetResponseDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewUpdateRequestDto;
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
        User seller=dealPost.getUser();

        dealReviewRepository.save(DealReview.builder()
                .buyer(buyer)
                .seller(seller)
                .content(content)
                .dealPost(dealPost).build());

    }

    public DealReviewGetResponseDto getResponseDto(Integer dealReviewId){
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()->new NoSuchElementException("해당 리뷰글이 없습니다. dealReviewId:"+dealReviewId));
        return DealReviewGetResponseDto.builder()
                .sellerId(dealReview.getSeller().getId())
                .buyerId(dealReview.getBuyer().getId())
                .content(dealReview.getContent())
                .createdDate(dealReview.getCreatedDate())
                .modifiedDate(dealReview.getModifiedDate())
                .dealPostId(dealReview.getDealPost().getId())
                .build();
    }

    public void update(DealReviewUpdateRequestDto requestDto){
        DealReview dealReview=dealReviewRepository.findById(requestDto.getDealReviewId())
                .orElseThrow(()->new NoSuchElementException("해당 리뷰글이 없습니다. reviewId:"+requestDto.getDealReviewId()));
        dealReview.setContent(requestDto.getContent());
    }
    public void delete(Integer dealReviewId) throws Exception{
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()->new NoSuchElementException("해당 리뷰글이 없습니다. dealReviewId:"+dealReviewId));
        dealReview.deleteRelation();
        dealReviewRepository.delete(dealReview);
    }
}
