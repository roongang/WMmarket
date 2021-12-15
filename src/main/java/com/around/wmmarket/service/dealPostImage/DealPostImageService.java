package com.around.wmmarket.service.dealPostImage;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_post_image.DealPostImageRepository;
import com.around.wmmarket.service.common.Constants;
import com.around.wmmarket.service.common.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DealPostImageService {
    private final FileHandler fileHandler;
    private final DealPostImageRepository dealPostImageRepository;

    @Transactional
    public void save(DealPost dealPost, List<MultipartFile> files) throws Exception{
        if(files.isEmpty()) return;
        List<DealPostImage> dealPostImages=fileHandler.parseFileInfo(dealPost,files);
        dealPostImageRepository.saveAll(dealPostImages);
    }
    public DealPostImage get(Integer dealPostImageId) {
        return dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new NoSuchElementException("해당 이미지가 없습니다. id:"+dealPostImageId));
    }

    @Transactional
    public void delete(Integer dealPostImageId) throws Exception{
        DealPostImage dealPostImage=dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new NoSuchElementException("해당 거래글 이미지가 없습니다. id:"+dealPostImageId));
        // 물리적인 삭제
        fileHandler.delete(Constants.dealPostImagePath,dealPostImage.getName());
        dealPostImage.deleteRelation();
        dealPostImageRepository.delete(dealPostImage);
    }
}
