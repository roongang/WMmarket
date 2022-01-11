package com.around.wmmarket.service.dealPostImage;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.FileHandler;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
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

@Service
@RequiredArgsConstructor
public class DealPostImageService {
    private final FileHandler fileHandler;
    private final DealPostImageRepository dealPostImageRepository;
    private final DealPostRepository dealPostRepository;

    @Transactional
    public void save(SignedUser signedUser,Integer dealPostId, List<MultipartFile> files) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_NOT_FOUND));
        if(!dealPost.getUser().getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_DEALPOST);
        // save
        List<DealPostImage> dealPostImages=fileHandler.parseFileInfo(dealPost,files);
        dealPostImageRepository.saveAll(dealPostImages);
    }
    public DealPostImage get(Integer dealPostImageId) {
        return dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_IMAGE_NOT_FOUND));
    }

    @Transactional
    public void delete(Integer dealPostImageId) {
        DealPostImage dealPostImage=dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new CustomException(ErrorCode.DEALPOST_IMAGE_NOT_FOUND));
        // 물리적인 삭제
        fileHandler.delete(Constants.dealPostImagePath,dealPostImage.getName());
        dealPostImageRepository.delete(dealPostImage);
    }
}
