package com.around.wmmarket.service.dealSuccess;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.deal_success.DealSuccessRepository;
import com.around.wmmarket.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class DealSuccessService {
    private final DealSuccessRepository dealSuccessRepository;

    @Transactional
    public DealSuccess save(User buyer, DealPost dealPost){
        return dealSuccessRepository.save(DealSuccess.builder()
                .buyer(buyer)
                .dealPost(dealPost).build());
    }
    public DealSuccess findById(Integer dealPostId){
        return dealSuccessRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEAL_SUCCESS_NOT_FOUND));
    }
    @Transactional
    public void delete(Integer dealSuccessId){
        DealSuccess dealSuccess=dealSuccessRepository.findById(dealSuccessId)
                .orElseThrow(()->new CustomException(ErrorCode.DEAL_SUCCESS_NOT_FOUND));
        dealSuccessRepository.delete(dealSuccess);
    }
}
