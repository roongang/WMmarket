package com.around.wmmarket.service.dealSuccess;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.deal_success.DealSuccessRepository;
import com.around.wmmarket.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class DealSuccessService {
    private final DealSuccessRepository dealSuccessRepository;

    public DealSuccess save(User buyer, DealPost dealPost){
        return dealSuccessRepository.save(DealSuccess.builder()
                .buyer(buyer)
                .dealPost(dealPost).build());
    }
    public DealSuccess findById(int dealPostId){
        return dealSuccessRepository.findById(dealPostId)
                .orElseThrow(()->new NoSuchElementException("해당 게시글 완료가 존재하지 않습니다. dealPost id:"+dealPostId));
    }
    public void delete(DealSuccess dealSuccess){
        dealSuccess.deleteRelation();
        dealSuccessRepository.delete(dealSuccess);
    }
}
