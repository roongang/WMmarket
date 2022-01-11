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
    @PreRemove
    public void deleteRelation(){
        if(this.dealPost!=null) this.dealPost.getDealPostImages().remove(this);
    }
}