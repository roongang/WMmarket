package com.around.wmmarket.domain.manner_review;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "manner_review")
@Entity
public class MannerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sellerId;

    @Column(nullable = false)
    private Integer buyerId;

    @Column(nullable = false)
    private Integer mannerCnt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Manner manner;

    @Builder
    public MannerReview(Integer sellerId,Integer buyerId,Integer mannerCnt,Manner manner){
        this.sellerId=sellerId;
        this.buyerId=buyerId;
        this.mannerCnt=mannerCnt;
        this.manner=manner;
    }
}
