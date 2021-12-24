package com.around.wmmarket.domain.deal_review;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deal_review")
@Entity
public class DealReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "SELLER_ID")
    private User seller;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "DEAL_POST_ID")
    private DealPost dealPost;

    @Builder
    public DealReview(User buyer,User seller,String content,DealPost dealPost){
        setBuyer(buyer);
        setSeller(seller);
        setDealPost(dealPost);
        this.content=content;
    }

    // setter
    public void setBuyer(User buyer){
        // 기존 관계 제거
        if(this.buyer!=null) this.buyer.getBuyDealReviews().remove(this);
        this.buyer=buyer;
        if(buyer!=null) buyer.getBuyDealReviews().add(this);
    }
    public void setSeller(User seller){
        if(this.seller!=null) this.seller.getSellDealReviews().remove(this);
        this.seller=seller;
        if(seller!=null) seller.getSellDealReviews().add(this);
    }
    public void setDealPost(DealPost dealPost){
        if(this.dealPost!=null) this.dealPost.getDealReviews().remove(this);
        this.dealPost=dealPost;
        if(dealPost!=null) dealPost.getDealReviews().add(this);
    }
    public void setContent(String content){this.content=content;}

    public void deleteRelation(){
        if(this.buyer!=null) this.buyer.getBuyDealReviews().remove(this);
        if(this.seller!=null) this.seller.getSellDealReviews().remove(this);
        if(this.dealPost!=null) this.dealPost.getDealReviews().remove(this);
    }
}