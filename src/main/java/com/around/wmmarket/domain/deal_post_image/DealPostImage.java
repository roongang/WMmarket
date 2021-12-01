package com.around.wmmarket.domain.deal_post_image;

import com.around.wmmarket.domain.deal_post.DealPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String path;

    @Column(nullable = false)
    private Integer dealId;

    @Builder
    public DealPostImage(String path,Integer dealId){
        this.path=path;
        this.dealId=dealId;
    }

    public void setDealPost(DealPost dealPost){
        this.dealPost=dealPost;
    }
}