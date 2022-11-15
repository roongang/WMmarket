package com.around.wmmarket.domain.deal_success;

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
@Table(name = "deal_success")
@Entity
public class DealSuccess extends BaseTimeEntity {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    // dealPost 와 1:1 식별관계임
    @MapsId
    @OneToOne
    @JoinColumn(name = "DEAL_POST_ID")
    private DealPost dealPost;

    @Builder
    public DealSuccess(DealPost dealPost,User buyer) {
        setDealPost(dealPost);
        setBuyer(buyer);
    }

    // setter
    public void setBuyer(User buyer) {
        if(this.buyer!=null) this.buyer.getDealSuccesses().remove(this);
        this.buyer=buyer;
        if(buyer!=null) buyer.getDealSuccesses().add(this);
    }
    public void setDealPost(DealPost dealPost){
        if(this.dealPost!=null) this.dealPost.setDealSuccess(null);
        this.dealPost=dealPost;
        if(dealPost!=null) dealPost.setDealSuccess(this);
    }
    // delete
    @PreRemove
    public void deleteRelation(){
        if(this.buyer!=null) this.buyer.getDealSuccesses().remove(this);
        if(this.dealPost!=null) this.dealPost.setDealSuccess(null);
    }
}
