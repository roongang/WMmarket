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

    @Builder
    public DealPostImage(String name,DealPost dealPost){
        this.name=name;
        setDealPost(dealPost);
    }
    // setter
    public void setDealPost(DealPost dealPost){
        if(this.dealPost!=null) this.dealPost.getDealPostImages().remove(this);
        this.dealPost=dealPost;
        if(dealPost!=null) dealPost.getDealPostImages().add(this);
    }
    // delete
    public void deleteRelation(){
        if(this.dealPost!=null) this.dealPost.getDealPostImages().remove(this);
    }
}