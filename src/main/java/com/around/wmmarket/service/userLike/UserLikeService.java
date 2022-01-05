package com.around.wmmarket.service.userLike;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.domain.user_like.UserLikeId;
import com.around.wmmarket.domain.user_like.UserLikeRepository;
import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserLikeService {
    private final UserLikeRepository userLikeRepository;
    private final UserRepository userRepository;
    private final DealPostRepository dealPostRepository;

    public void save(Integer userId,Integer dealPostId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(userLikeRepository.existsById(UserLikeId.builder()
                .userId(user.getId())
                .dealPostId(dealPost.getId()).build())) throw new CustomException(ErrorCode.DUPLICATE_USER_LIKE);
        userLikeRepository.save(UserLike.builder()
                .user(user)
                .dealPost(dealPost).build());
    }
    public UserLike get(Integer userId,Integer dealPostId){
        UserLikeId userLikeId= UserLikeId.builder()
                .userId(userId)
                .dealPostId(dealPostId).build();
        return userLikeRepository.findById(userLikeId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_LIKE_NOT_FOUND));
    }

    public void delete(Integer userId,Integer dealPostId){
        UserLikeId userLikeId= UserLikeId.builder()
                .userId(userId)
                .dealPostId(dealPostId).build();
        UserLike userLike=userLikeRepository.findById(userLikeId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_LIKE_NOT_FOUND));
        userLike.deleteRelation();
        userLikeRepository.delete(userLike);
    }
}
