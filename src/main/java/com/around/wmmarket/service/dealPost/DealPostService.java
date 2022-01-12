package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveRequestDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostSaveResponseDto;
import com.around.wmmarket.controller.dto.dealPost.DealPostUpdateRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
import com.around.wmmarket.service.dealSuccess.DealSuccessService;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealPostService {
    // repo
    private final DealPostRepository dealPostRepository;
    private final UserRepository userRepository;
    // service
    private final UserService userService;
    private final DealPostImageService dealPostImageService;
    private final DealSuccessService dealSuccessService;

    @Transactional
    public DealPostSaveResponseDto save(SignedUser signedUser, DealPostSaveRequestDto requestDto) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user = userRepository.findByEmail(signedUser.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // save
        DealPost dealPost = DealPost.builder()
                .user(user)
                .category(Category.valueOf(requestDto.getCategory()))
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .content(requestDto.getContent())
                .dealState(DealState.ONGOING).build();
        if(requestDto.getFiles()!=null) dealPostImageService.save(dealPost,requestDto.getFiles());
        return new DealPostSaveResponseDto(dealPostRepository.save(dealPost).getId());
    }

    public DealPostGetResponseDto getDealPostDto(Integer id) {
        DealPost dealPost=dealPostRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        DealPostGetResponseDto responseDto=DealPostGetResponseDto.builder()
                .category(dealPost.getCategory())
                .title(dealPost.getTitle())
                .price(dealPost.getPrice())
                .content(dealPost.getContent())
                .dealState(dealPost.getDealState())
                .build();
        if(dealPost.getUser()!=null) responseDto.setUserEmail(dealPost.getUser().getEmail()); // user 가 삭제되는 경우 null
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
        return dealPost.getDealPostImages().stream()
                .map(DealPostImage::getId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(SignedUser signedUser,Integer dealPostId,DealPostUpdateRequestDto requestDto){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(dealPost.getUser()==null) throw new CustomException(ErrorCode.DEALPOST_USER_NOT_FOUND);
        if(!dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        // update
        if(requestDto.getCategory()!=null) dealPost.setCategory(Category.valueOf(requestDto.getCategory()));
        if(requestDto.getTitle()!=null) dealPost.setTitle(requestDto.getTitle());
        if(requestDto.getPrice()!=null) dealPost.setPrice(requestDto.getPrice());
        if(requestDto.getContent()!=null) dealPost.setContent(requestDto.getContent());
        // dealState 변경
        if(requestDto.getDealState()!=null){
            // check
            if(dealPost.getDealState().name().equals(requestDto.getDealState())) throw new CustomException(ErrorCode.DEALPOST_STATE_SAME);
            User buyer = null;
            if(requestDto.getBuyerId()!=null) buyer=userRepository.findById(requestDto.getBuyerId()).orElseThrow(()->new CustomException(ErrorCode.BUYER_NOT_FOUND));
            if(requestDto.getBuyerId()!=null && userService.getUserEmail(requestDto.getBuyerId()).equals(signedUser.getUsername())) throw new CustomException(ErrorCode.SAME_BUYER_SELLER);

            // update dealState
            this.updateDealState(dealPost,DealState.valueOf(requestDto.getDealState()),buyer);
        }
    }

    @Transactional
    public void updateDealState(DealPost dealPost, DealState dealState, User buyer){
        // dealSuccess save, ?->DONE
        if(dealState.equals(DealState.DONE)){
            if(buyer==null) throw new CustomException(ErrorCode.BUYER_NOT_FOUND);
            dealSuccessService.save(buyer,dealPost);
        }
        // dealSuccess delete, DONE->?
        else if(dealPost.getDealState().equals(DealState.DONE)){
            dealSuccessService.delete(dealPost.getId());
        }
        dealPost.setDealState(dealState);
    }

    @Transactional
    public void delete(SignedUser signedUser,Integer dealPostId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(dealPost.getUser()==null) throw new CustomException(ErrorCode.DEALPOST_USER_NOT_FOUND);
        if(!dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        // delete
        dealPost.getDealPostImages().stream()
                .map(DealPostImage::getId)
                .forEach(dealPostImageService::delete);
        dealPostRepository.delete(dealPost);
    }

    @Transactional
    public void deleteImage(Integer dealPostImageId){
        dealPostImageService.delete(dealPostImageId);
    }
}
