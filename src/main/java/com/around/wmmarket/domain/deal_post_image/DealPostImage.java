package com.around.wmmarket.domain.deal_post_image;

import com.around.wmmarket.domain.deal_post.DealPost;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deal_post_image")
@Entity
public class DealPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DEAL_POST_ID")
    private DealPost dealPost;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer dealId;

    @Builder
    public DealPostImage(String name,Integer dealId){
        this.name=name;
        this.dealId=dealId;
    }

    public void setDealPost(DealPost dealPost){
        this.dealPost=dealPost;
    }
    public MultipartFile getMultipartFile() throws Exception{
        String absPath=new File("").getAbsolutePath()+File.separator;
        String path=absPath+"images"+File.separator+File.separator+"dealPostImages"+File.separator;
        String name=this.name;
        File file=new File(path+name);
        FileItem fileItem=new DiskFileItem(name,
                Files.probeContentType(file.toPath()),
                false,
                file.getName(),
                (int)file.length(),
                file.getParentFile());
        InputStream input =new FileInputStream(file);
        OutputStream output= fileItem.getOutputStream();
        IOUtils.copy(input,output);
        return new CommonsMultipartFile(fileItem);
    }
}