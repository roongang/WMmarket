package com.around.wmmarket.service.dealPostImage;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_post_image.DealPostImageRepository;
import com.around.wmmarket.service.common.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DealPostImageService {
    private final FileHandler fileHandler;
    private final DealPostImageRepository dealPostImageRepository;

    public void save(Integer dealPostId, List<MultipartFile> files) throws Exception{
        if(files.isEmpty()) return;
        fileHandler.save(dealPostId,files);
    }
    public DealPostImage get(Integer dealPostImageId) {
        return dealPostImageRepository.findById(dealPostImageId)
                .orElseThrow(()->new NoSuchElementException("해당 이미지가 없습니다. id:"+dealPostImageId));
    }
}
