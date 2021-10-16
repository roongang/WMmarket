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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dealPostId;

    @ManyToOne
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    // MapsId 대신 바로 ID를 쓰면 동등성 비교를 처리해야함.
    @MapsId
    @OneToOne
    @JoinColumn(name = "DEAL_POST_ID")
    private DealPost dealPost;

    @Builder
    public DealSuccess(User buyer){
        this.buyer=buyer;
    }
}
