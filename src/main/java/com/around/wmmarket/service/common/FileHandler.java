package com.around.wmmarket.service.common;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FileHandler {
    private SimpleDateFormat simpleDateFormat;

    public List<DealPostImage> parseFileInfo(DealPost dealPost, List<MultipartFile> files) throws Exception{
        List<DealPostImage> dealPostImages=new ArrayList<>();

        if(CollectionUtils.isEmpty(files)) return null;
        // 절대경로
        String absPath=new File("").getAbsolutePath()+File.separator+File.separator;
        // 저장할 세부경로
        String resourcePath="src"+File.separator+File.separator
                +"main"+File.separator+File.separator
                +"resources"+File.separator+File.separator;
        String dirPath=resourcePath
                +"images"+File.separator+File.separator
                +"dealPostImages"+File.separator;
        File dir=new File(absPath+dirPath);
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
                    .dealId(dealPost.getId())
                    .name(fileName)
                    .dealPost(dealPost).build();
            // 이미지를 리스트에 추가
            dealPostImages.add(dealPostImage);

            // transferTo 방식
            /*File file=new File(absPath+dirPath+File.separator+fileName);
            multipartFile.transferTo(file);*/
            // getBytes 방식
            write(multipartFile,Paths.get(absPath+dirPath),fileName);
        }
        return dealPostImages;
    }

    public void write(MultipartFile multipartFile, Path dir,String fileName) {
        Path filepath = Paths.get(dir.toString(), fileName);
        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(multipartFile.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
