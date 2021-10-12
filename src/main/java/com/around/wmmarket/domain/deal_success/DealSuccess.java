package com.around.wmmarket.domain.deal_success;

import com.around.wmmarket.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "deal_success")
@Entity
public class DealSuccess extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dealPostId;

    @Column(nullable = false)
    private Integer buyerId;

    @Builder
    public DealSuccess(Integer buyerId){
        this.buyerId=buyerId;
    }
}
