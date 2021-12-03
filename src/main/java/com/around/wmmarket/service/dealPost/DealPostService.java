package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.common.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealPostService {
    private final UserRepository userRepository;
    private final DealPostRepository dealPostRepository;
    private final FileHandler fileHandler;

    public void save(SignedUser signedUser,DealPostSaveRequestDto requestDto,List<MultipartFile> multipartFiles) throws Exception{
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
        if(!CollectionUtils.isEmpty(multipartFiles)) fileHandler.save(dealPost.getId(),multipartFiles);
    }
}
