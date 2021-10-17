package com.around.wmmarket.domain.manner_review;

import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "manner_review")
@Entity
public class MannerReview {
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
    private Integer mannerCnt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Manner manner;

    @Builder
    public MannerReview(User seller,User buyer,Integer mannerCnt,Manner manner){
        this.seller=seller;
        this.buyer=buyer;
        this.mannerCnt=mannerCnt;
        this.manner=manner;
    }
}
