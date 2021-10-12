package com.around.wmmarket.domain.deal_review;

import com.around.wmmarket.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "deal_review")
@Entity
public class DealReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer buyerId;

    @Column(nullable = false)
    private Integer sellerId;

    @Column(nullable = false)
    private String content;

    @Builder
    public DealReview(Integer buyerId,Integer sellerId){
        this.buyerId=buyerId;
        this.sellerId=sellerId;
    }
}