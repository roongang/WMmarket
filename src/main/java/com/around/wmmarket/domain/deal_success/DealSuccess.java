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
    private Integer dealPostId;

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
        this.dealPost=dealPost;
        this.buyer=buyer;
    }

    // setter
    public void setBuyer(User buyer) {this.buyer=buyer;}
}
