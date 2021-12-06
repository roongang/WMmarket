package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.controller.dto.DealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.DealPost.DealPostSaveRequestDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DealPostService {
    private final UserRepository userRepository;
    private final DealPostRepository dealPostRepository;
    private final DealPostImageService dealPostImageService;

    public void save(SignedUser signedUser,DealPostSaveRequestDto requestDto) throws Exception{
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
        dealPostImageService.save(dealPost.getId(),requestDto.getFiles());
    }

    public DealPostGetResponseDto get(Integer id) {
        DealPost dealPost=dealPostRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("해당 게시글이 없습니다. id:"+id));
        return DealPostGetResponseDto.builder()
                .userEmail(dealPost.getUser().getEmail())
                .category(dealPost.getCategory())
                .title(dealPost.getTitle())
                .price(dealPost.getPrice())
                .content(dealPost.getContent())
                .dealState(dealPost.getDealState())
                .build();
    }

    public boolean isDealPostAuthor(SignedUser signedUser,Integer dealPostId){
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new NoSuchElementException("해당 게시글이 없습니다. id:"+dealPostId));
        return signedUser.getUsername().equals(dealPost.getUser().getEmail());
    }

    public List<Integer> getImages(Integer dealPostId){
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new NoSuchElementException("해당 게시글이 없습니다. id:"+dealPostId));
        List<DealPostImage> dealPostImages=dealPost.getDealPostImages();
        List<Integer> images=new ArrayList<>();
        for(DealPostImage dealPostImage:dealPostImages){
            images.add(dealPostImage.getId());
        }
        return images;
    }
}
