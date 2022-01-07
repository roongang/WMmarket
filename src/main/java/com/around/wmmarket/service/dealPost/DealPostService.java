package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
import com.around.wmmarket.service.dealSuccess.DealSuccessService;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DealPostService {
    // repo
    private final DealPostRepository dealPostRepository;
    // service
    private final UserService userService;
    private final DealPostImageService dealPostImageService;
    private final DealSuccessService dealSuccessService;

    @Transactional
    public void save(Integer userId,DealPostSaveRequestDto requestDto) {
        User user = userService.getUser(userId);
        DealPost dealPost = DealPost.builder()
                .user(user)
                .category(Category.valueOf(requestDto.getCategory()))
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .content(requestDto.getContent())
                .dealState(DealState.ONGOING).build();
        if(requestDto.getFiles()!=null) dealPostImageService.save(dealPost,requestDto.getFiles());
        dealPostRepository.save(dealPost);
    }

    // TODO : service 메소드 이름을 이렇게 지어야하나
    public DealPostGetResponseDto getDealPostGetResponseDto(Integer id) {
        DealPost dealPost=dealPostRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        DealPostGetResponseDto responseDto=DealPostGetResponseDto.builder()
                .category(dealPost.getCategory())
                .title(dealPost.getTitle())
                .price(dealPost.getPrice())
                .content(dealPost.getContent())
                .dealState(dealPost.getDealState())
                .build();
        if(dealPost.getUser()!=null) responseDto.setUserEmail(dealPost.getUser().getEmail());
        return responseDto;
    }
    public DealPost getDealPost(Integer id){
        return dealPostRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
    }

    public boolean isDealPostAuthor(SignedUser signedUser,Integer dealPostId){
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        return signedUser.getUsername().equals(dealPost.getUser().getEmail());
    }

    public List<Integer> getImages(Integer dealPostId){
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        List<DealPostImage> dealPostImages=dealPost.getDealPostImages();
        List<Integer> images=new ArrayList<>();
        for(DealPostImage dealPostImage:dealPostImages){
            images.add(dealPostImage.getId());
        }
        return images;
    }

    @Transactional
    public void update(Integer dealPostId,DealPostUpdateRequestDto requestDto){
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(requestDto.getCategory()!=null) dealPost.setCategory(Category.valueOf(requestDto.getCategory()));
        if(requestDto.getTitle()!=null) dealPost.setTitle(requestDto.getTitle());
        if(requestDto.getPrice()!=null) dealPost.setPrice(requestDto.getPrice());
        if(requestDto.getContent()!=null) dealPost.setContent(requestDto.getContent());
        // dealState 변경
        if(requestDto.getDealState()!=null){
            // TODO : dealSuccess service 따로 구현!!
            User buyer = null;
            if(requestDto.getBuyerId()!=null){
                buyer=userService.getUser(requestDto.getBuyerId());
            }
            // dealSuccess 저장, ? -> DONE
            if(dealPost.getDealState()!=DealState.DONE
                    && Objects.equals(requestDto.getDealState(), DealState.DONE.name())
                    && buyer!=null){
                dealPost.setDealSuccess(dealSuccessService.save(buyer,dealPost));
            }
            // dealSuccess 수정, DONE && buyerId -> ?
            else if(dealPost.getDealState()==DealState.DONE
                    && Objects.equals(requestDto.getDealState(), DealState.DONE.name())
                    && buyer!=null
                    && !Objects.equals(dealPost.getDealSuccess().getBuyer(), buyer)){
                DealSuccess dealSuccess=dealSuccessService.findById(dealPost.getId());
                dealSuccess.setBuyer(buyer);
            }
            // dealSuccess 삭제, DONE -> ?
            else if(dealPost.getDealState()==DealState.DONE
                    && !requestDto.getDealState().equals(DealState.DONE.name())){
                dealSuccessService.delete(dealSuccessService.findById(dealPost.getId()));
                dealPost.setDealSuccess(null);
            }
            dealPost.setDealState(DealState.valueOf(requestDto.getDealState()));
        }
    }

    @Transactional
    public void delete(DealPost dealPost) {
        // TODO : 연관관계가 추가된다면 로직을 추가해야함
        for(DealPostImage dealPostImage:dealPost.getDealPostImages()){
            dealPostImage.deleteRelation();
            dealPostImageService.delete(dealPostImage.getId());
        }
        dealPost.deleteRelation();
        dealPostRepository.delete(dealPost);
    }

    @Transactional
    public void deleteImage(Integer dealPostImageId){
        dealPostImageService.delete(dealPostImageId);
    }
}
