package com.around.wmmarket.domain.deal_review;

import com.around.wmmarket.domain.BaseTimeEntity;
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
    @JoinColumn(name = "SELLER_ID")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    @Column(nullable = false)
    private String content;

    @Builder
    public DealReview(User seller,User buyer,String content){
        this.seller=seller;
        this.buyer=buyer;
        this.content=content;
    }
}