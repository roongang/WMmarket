package com.around.wmmarket.service.dealPostImage;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.FileHandler;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPostImage.DealPostImageSaveResponseDto;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_post_image.DealPostImageRepository;
import com.around.wmmarket.domain.user.SignedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealPostImageService {
    private final FileHandler fileHandler;
    private final DealPostImageRepository dealPostImageRepository;
    private final DealPostRepository dealPostRepository;

    public DealPostImageSaveResponseDto save(SignedUser signedUser, Integer dealPostId, List<MultipartFile> files) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(!dealPostRepository.existsById(dealPostId)) throw new CustomException(ErrorCode.DEALPOST_NOT_FOUND);
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(!dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        // save
        List<DealPostImage> dealPostImages=this.save(dealPost,files);
        return DealPostImageSaveResponseDto.builder()
                .ids(dealPostImages.stream().map(DealPostImage::getId).collect(Collectors.toList()))
                .names(dealPostImages.stream().map(DealPostImage::getName).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public List<DealPostImage> save(DealPost dealPost,List<MultipartFile> files){
        List<DealPostImage> dealPostImages=fileHandler.parseFileInfo(dealPost,files);
        return dealPostImageRepository.saveAll(dealPostImages);
    }

    public DealPostImage get(Integer dealPostImageId) {
        return dealPostImageRepository.findById(dealPostImageId)
                .orElse(null);
    }

    public void delete(SignedUser signedUser,Integer dealPostImageId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealPostImage dealPostImage=dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_IMAGE_NOT_FOUND));
        if(dealPostImage.getDealPost()==null) throw new CustomException(ErrorCode.DEALPOST_NOT_FOUND);
        if(dealPostImage.getDealPost().getUser()==null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        if(!dealPostImage.getDealPost().getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        // 물리적인 삭제
        this.delete(dealPostImageId);
    }

    @Transactional
    public void delete(Integer dealPostImageId){
        // 물리적인 삭제
        DealPostImage dealPostImage=dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_IMAGE_NOT_FOUND));
        fileHandler.delete(Constants.dealPostImagePath,dealPostImage.getName());
        dealPostImageRepository.delete(dealPostImage);
    }
}
