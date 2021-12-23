package com.around.wmmarket.service.userLike;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.domain.user_like.UserLikeId;
import com.around.wmmarket.domain.user_like.UserLikeRepository;
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

    public void save(String userEmail,Integer dealPostId){
        User user=userRepository.findByEmail(userEmail)
                .orElseThrow(()->new UsernameNotFoundException("해당 유저가 존재하지 않습니다. email:"+userEmail));
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new NoSuchElementException("해당 dealPost 가 존재하지 않습니다. id:"+dealPostId));
        userLikeRepository.save(UserLike.builder()
                .user(user)
                .dealPost(dealPost).build());
    }
    public UserLike get(Integer userId,Integer dealPostId){
        UserLikeId userLikeId= UserLikeId.builder()
                .userId(userId)
                .dealPostId(dealPostId).build();
        return userLikeRepository.findById(userLikeId)
                .orElseThrow(()->new NoSuchElementException("not found userLike; userId:"+userId+", dealPostId:"+dealPostId));
    }

    public void delete(Integer userId,Integer dealPostId){
        UserLikeId userLikeId= UserLikeId.builder()
                .userId(userId)
                .dealPostId(dealPostId).build();
        UserLike userLike=userLikeRepository.findById(userLikeId)
                .orElseThrow(()->new NoSuchElementException("not found userLike; userId:"+userId+", dealPostId:"+dealPostId));
        userLike.deleteRelation();
        userLikeRepository.delete(userLike);
    }
}
