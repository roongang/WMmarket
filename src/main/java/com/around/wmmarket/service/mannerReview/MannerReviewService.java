package com.around.wmmarket.service.mannerReview;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveResponseDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.manner_review.MannerReview;
import com.around.wmmarket.domain.manner_review.MannerReviewRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MannerReviewService {
    private final MannerReviewRepository mannerReviewRepository;
    private final UserRepository userRepository;

    // save
    public MannerReviewSaveResponseDto save(SignedUser signedUser, MannerReviewSaveRequestDto requestDto){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User buyer=userRepository.findById(requestDto.getBuyerId())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND,"구매자 회원이 존재하지 않습니다,id : "+requestDto.getBuyerId()));
        User seller=userRepository.findById(requestDto.getSellerId())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND,"판매자 회원이 존재하지 않습니다,id : "+requestDto.getSellerId()));
        if(!buyer.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_MANNER_REVIEW,signedUser.getUsername()+"는 구매자와 일치하지 않습니다.");
        // deal post
        boolean isBuyer=false;
        for(DealPost dealPost:seller.getDealPosts()){
            if(dealPost.getDealSuccess().getBuyer().equals(buyer)){
                isBuyer=true;
                break;
            }
        }
        if(!isBuyer) throw new CustomException(ErrorCode.BUYER_NOT_FOUND,"구매자를 찾을 수 없습니다. id : "+requestDto.getBuyerId());

        // save
        MannerReview mannerReview=MannerReview.builder()
                .buyer(buyer)
                .seller(seller)
                .manner(requestDto.getManner()).build();
        mannerReviewRepository.save(mannerReview);
        return new MannerReviewSaveResponseDto(mannerReview.getId());
    }
}
