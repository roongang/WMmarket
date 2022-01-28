package com.around.wmmarket.service.mannerReview;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveResponseDto;
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
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!buyer.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_MANNER_REVIEW);

        // save
        MannerReview mannerReview=MannerReview.builder().build();
        return new MannerReviewSaveResponseDto(mannerReview.getId());
    }
}
