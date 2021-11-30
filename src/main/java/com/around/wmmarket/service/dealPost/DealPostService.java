package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DealPostService {
    private final UserRepository userRepository;
    private final DealPostRepository dealPostRepository;

    public void save(SignedUser signedUser,DealPostSaveRequestDto requestDto){
        User user = userRepository.findByEmail(signedUser.getUsername())
                .orElseThrow(()->new UsernameNotFoundException("not found : "+signedUser.getUsername()));
        DealPost dealPost = DealPost.builder()
                .user(user)
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .content(requestDto.getContent())
                .dealState(requestDto.getDealState()).build();
        dealPostRepository.save(dealPost);
    }
}
