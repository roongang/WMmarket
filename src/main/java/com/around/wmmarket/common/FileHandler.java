package com.around.wmmarket.common;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
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

    // for dealPostImage
    public List<DealPostImage> parseFileInfo(DealPost dealPost, List<MultipartFile> files) {
        List<DealPostImage> dealPostImages=new ArrayList<>();

        if(files==null||CollectionUtils.isEmpty(files)) return null;

        File dir=Constants.dealPostImagePath.toFile();
        if(!dir.exists()) dir.mkdirs();

        for(MultipartFile multipartFile:files){
            if(!isPossibleExtension(multipartFile)) continue;
            String fileName=getRandomFileName(multipartFile);
            // 이미지 엔티티 생성
            DealPostImage dealPostImage=DealPostImage.builder()
                    .name(fileName)
                    .dealPost(dealPost).build();
            // 이미지를 리스트에 추가
            dealPostImages.add(dealPostImage);

            // getBytes 방식
            write(multipartFile,dir.toPath(),fileName);
        }
        return dealPostImages;
    }

    public String parseUserImage(MultipartFile image){
        if(image.isEmpty()) return null;
        // mkdir
        File dir=Constants.userImagePath.toFile();
        if(!dir.exists()) dir.mkdirs();
        // extension check
        if(!isPossibleExtension(image)) return null;
        String fileName=getRandomFileName(image);
        // write
        write(image,dir.toPath(),fileName);
        return fileName;
    }

    private boolean isPossibleExtension(MultipartFile multipartFile){
        String contentType=multipartFile.getContentType();

        // 확장자명이 없으면 패스
        if(ObjectUtils.isEmpty(contentType)) return false;
        // 확장자 처리
        if(contentType.contains("image/jpeg")) return true;
        else if(contentType.contains("image/png")) return true;
        else return false;
    }

    private String getFileExtension(MultipartFile multipartFile){
        String fileExtension=null;
        String contentType=multipartFile.getContentType();
        // 확장자명이 없으면 패스
        if(ObjectUtils.isEmpty(contentType)) return null;
        // 확장자 처리
        if(contentType.contains("image/jpeg")) fileExtension=".jpg";
        else if(contentType.contains("image/png")) fileExtension=".png";

        return fileExtension;
    }

    private String getRandomFileName(MultipartFile multipartFile){
        String fileExtension=getFileExtension(multipartFile);
        // 중복방지를 위해 나노시간을 추가
        simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        String nowTime=simpleDateFormat.format(new Date());
        return nowTime+"_"+System.nanoTime()+fileExtension;
    }

    public void write(MultipartFile multipartFile, Path dir,String fileName) {
        Path filepath = Paths.get(dir.toString(),fileName);
        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(multipartFile.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void delete(Path dir,String fileName) {
        File file=Paths.get(dir.toString(),fileName).toFile();
        if(!file.delete()) throw new CustomException(ErrorCode.FILE_DELETE_FAIL);
    }
}
