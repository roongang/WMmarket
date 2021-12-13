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

    @Column(nullable = false)
    private String content;

    // seller 를 dealPost 로 대체
    @ManyToOne
    @JoinColumn(name = "DEAL_POST_ID")
    private DealPost dealPost;

    @Builder
    public DealReview(User buyer,String content,DealPost dealPost){
        this.buyer=buyer;
        this.content=content;
        this.dealPost=dealPost;
    }
}