package com.around.wmmarket.service.dealReview;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealReview.DealReviewGetResponseDto;
import com.around.wmmarket.controller.dto.dealReview.DealReviewUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_review.DealReviewRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class DealReviewService {
    // repo
    private final DealReviewRepository dealReviewRepository;
    private final DealPostRepository dealPostRepository;
    // service
    private final UserService userService;

    @Transactional
    public void save(SignedUser signedUser, String content, Integer dealPostId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(dealPost.getUser()==null) throw new CustomException(ErrorCode.DEALPOST_USER_NOT_FOUND);
        if(dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEAL_REVIEW);
        // check dealState && dealSuccess
        if(!dealPost.getDealState().equals(DealState.DONE)
        || dealPost.getDealSuccess()==null
        || dealPost.getDealSuccess().getBuyer()==null) throw new CustomException(ErrorCode.DEALPOST_NOT_DONE);
        if(!dealPost.getDealSuccess().getBuyer().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEAL_REVIEW);

        // save
        User buyer=userService.getUser(signedUser.getUsername());
        User seller=dealPost.getUser();
        dealReviewRepository.save(DealReview.builder()
                .buyer(buyer)
                .seller(seller)
                .content(content)
                .dealPost(dealPost).build());
    }

    public DealReviewGetResponseDto getDealReviewDto(Integer dealReviewId){
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElse(null);
        if(dealReview==null) return null;
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

    public void update(SignedUser signedUser,Integer dealReviewId,DealReviewUpdateRequestDto requestDto){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        if(!dealReview.getBuyer().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEAL_REVIEW);
        // update
        dealReview.setContent(requestDto.getContent());
    }
    public void delete(Integer dealReviewId) {
        DealReview dealReview=dealReviewRepository.findById(dealReviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.DEAL_REVIEW_NOT_FOUND));
        dealReview.deleteRelation();
        dealReviewRepository.delete(dealReview);
    }
}
