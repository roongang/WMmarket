package com.around.wmmarket.service.common;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_post_image.DealPostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class FileHandler {
    private final DealPostRepository dealPostRepository;
    private final DealPostImageRepository dealPostImageRepository;
    private SimpleDateFormat simpleDateFormat;

    public void save(Integer dealPostId, List<MultipartFile> files) throws Exception{
        DealPost dealPost=dealPostRepository.findById(dealPostId)
                .orElseThrow(() -> new NoSuchElementException("no such dealPost ID:"+dealPostId));
        List<DealPostImage> dealPostImages=dealPost.getDealPostImages();

        if(CollectionUtils.isEmpty(files)) return;
        // 절대경로
        String absPath=new File("").getAbsolutePath()+File.separator+File.separator;
        // 저장할 세부경로
        String dirPath="dealPostImages"+File.separator;
        File dir=new File(dirPath);
        // 디렉토리가 존재하지 않으면 생성
        if(!dir.exists()) dir.mkdirs();

        for(MultipartFile multipartFile:files){
            String originFileExtension;
            String contentType=multipartFile.getContentType();
            // 확장자명이 없으면 패스
            if(ObjectUtils.isEmpty(contentType)) continue;
            // 확장자 처리
            if(contentType.contains("image/jpeg")) originFileExtension=".jpg";
            else if(contentType.contains("image/png")) originFileExtension=".png";
            else continue; // 다른 확장자는 저장안함

            // 중복방지를 위해 나노시간을 추가
            simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
            String nowTime=simpleDateFormat.format(new Date());
            String fileName= nowTime+"_"+System.nanoTime()+originFileExtension;
            // 이미지 엔티티 생성
            DealPostImage dealPostImage=DealPostImage.builder()
                    .dealId(dealPostId)
                    .path(fileName).build();
            dealPostImage.setDealPost(dealPost);
            dealPostImageRepository.save(dealPostImage);
            // 이미지를 리스트에 추가
            dealPostImages.add(dealPostImage);

            // 물리적 저장
            File file=new File(absPath+dirPath+File.separator+fileName);
            multipartFile.transferTo(file);
            // 읽기 쓰기 권한 설정
            file.setReadable(true);
            file.setWritable(true);
        }
    }
}
