package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@NoArgsConstructor
@Table(name = "deal_post")
@Entity
public class DealPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer categoryId;

    private Integer dealPostImageId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)  //datetime 어떻게 해야될지 확인 필요
    private Date pullingDate;

    @Column(nullable = false)
    private Integer pullingCnt;

    @Column(nullable = false)
    private Character dealState;

    @Builder
    public DealPost(Integer userId,Integer categoryId,Integer dealPostImageId,String title,Integer price,String content,Date pullingDate,Integer pullingCnt,Character dealState){
        this.userId=userId;
        this.categoryId=categoryId;
        this.dealPostImageId=dealPostImageId;
        this.title=title;
        this.price=price;
        this.content=content;
        this.pullingDate=pullingDate;
        this.pullingCnt=pullingCnt;
        this.dealState=dealState;
    }
}
