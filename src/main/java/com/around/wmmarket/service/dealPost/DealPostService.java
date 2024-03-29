package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.*;
import com.around.wmmarket.controller.dto.dealPostImage.DealPostImageGetResponseDto;
import com.around.wmmarket.domain.deal_post.*;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.keyword.Keyword;
import com.around.wmmarket.domain.keyword.KeywordRepository;
import com.around.wmmarket.domain.notification.NotificationType;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
import com.around.wmmarket.service.dealSuccess.DealSuccessService;
import com.around.wmmarket.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealPostService {
    // repo
    private final DealPostRepository dealPostRepository;
    private final DealPostQueryRepository dealPostQueryRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    // service
    private final DealPostImageService dealPostImageService;
    private final DealSuccessService dealSuccessService;
    private final NotificationService notificationService;

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
        DealPostSaveResponseDto response=DealPostSaveResponseDto.builder()
                .id(dealPostRepository.save(dealPost).getId())
                .build();
        // 거래 글 알림 전송
        sendDealPostNotification(dealPost);
        return response;
    }

    public DealPostGetResponseDto getDealPostDto(Integer id) {
        DealPost dealPost=dealPostRepository.findById(id)
                .orElse(null);
        if(dealPost==null) return null;
        return DealPostGetResponseDto.builder()
                .id(dealPost.getId())
                .userId(dealPost.getUser()!=null?dealPost.getUser().getId():null)
                .userNickname(dealPost.getUser()!=null?dealPost.getUser().getNickname():null)
                .category(dealPost.getCategory())
                .title(dealPost.getTitle())
                .price(dealPost.getPrice())
                .content(dealPost.getContent())
                .dealState(dealPost.getDealState())
                .createdDate(dealPost.getCreatedDate())
                .modifiedDate(dealPost.getModifiedDate())
                .imagesName(dealPost.getDealPostImages().stream()
                        .map(DealPostImage::getName).collect(Collectors.toList()))
                .imagesId(dealPost.getDealPostImages().stream()
                        .map(DealPostImage::getId).collect(Collectors.toList()))
                .viewCnt(dealPost.getViewCnt())
                .pullingCnt(dealPost.getPullingCnt())
                .pullingDate(dealPost.getPullingDate())
                .build();
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

    public List<DealPostImageGetResponseDto> getImages(Integer dealPostId){
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        return dealPost.getDealPostImages().stream()
                .map(dealPostImage-> DealPostImageGetResponseDto.builder()
                        .id(dealPostImage.getId())
                        .name(dealPostImage.getName())
                        .build())
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
            if(requestDto.getBuyerId()!=null){
                buyer=userRepository.findById(requestDto.getBuyerId()).orElseThrow(()->new CustomException(ErrorCode.BUYER_NOT_FOUND));
                if(buyer.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.SAME_BUYER_SELLER);
            }

            // update dealState
            this.updateDealState(dealPost,DealState.valueOf(requestDto.getDealState()),buyer);
        }
        if(requestDto.getViewCnt()!=null) dealPost.increaseViewCnt(requestDto.getViewCnt());
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
    public void increaseViewCnt(Integer dealPostId,Integer viewCnt){
        // check
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        // update
        dealPost.increaseViewCnt(viewCnt);
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
        List<Integer> dealPostImageIdList=dealPost.getDealPostImages().stream()
                .map(DealPostImage::getId)
                .collect(Collectors.toList());
        dealPostImageIdList.stream()
                .forEach(dealPostImageService::delete);
        dealPostRepository.delete(dealPost);
    }

    @Transactional
    public void deleteImage(Integer dealPostImageId){
        dealPostImageService.delete(dealPostImageId);
    }

    public Slice<DealPostGetResponseDto> findByFilter(DealPostSearchRequestDto requestDto){
        // filtering
        Map<String,String> filter=new HashMap<>();
        filter.put("page",requestDto.getPage());
        filter.put("size",requestDto.getSize());
        filter.put("sort",requestDto.getSort());
        filter.put("userId",requestDto.getUserId());
        filter.put("category",requestDto.getCategory());
        filter.put("title",requestDto.getTitle());
        filter.put("price",requestDto.getPrice());
        filter.put("viewCnt",requestDto.getViewCnt());
        filter.put("content",requestDto.getContent());
        filter.put("dealState",requestDto.getDealState());
        filter.put("createdDate",requestDto.getCreatedDate());
        filter.put("modifiedDate",requestDto.getModifiedDate());
        filter.put("pullingCnt",requestDto.getPullingCnt());
        filter.put("pullingDate",requestDto.getPullingDate());
        return dealPostQueryRepository.findByFilter(filter);
    }

    public void pull(SignedUser signedUser,Integer dealPostId){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(dealPost.getUser()==null) throw new CustomException(ErrorCode.DEALPOST_USER_NOT_FOUND);
        if(!dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        // pull
        dealPost.increasePullingCnt();
    }

    @Async
    public void sendDealPostNotification(DealPost dealPost){
        keywordRepository.findAll().stream()
                .filter(keyword ->
                        dealPost.getTitle().contains(keyword.getWord())
                        || dealPost.getContent().contains(keyword.getWord())
                        || dealPost.getCategory().name().contains(keyword.getWord()))
                .map(Keyword::getUser)
                .collect(Collectors.toSet())
                .forEach(user -> notificationService.send(user
                        , NotificationType.KEYWORD
                        ,"키워드로 등록된 게시글이 올라왔습니다!"
                        , Constants.API_PATH +"/deal-posts/"+dealPost.getId()
                        ,NotificationType.KEYWORD.name()));
    }
}
