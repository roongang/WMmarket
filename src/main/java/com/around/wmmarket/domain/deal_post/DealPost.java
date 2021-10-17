package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deal_post")
@Entity
public class DealPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime pullingDate;

    @Column(nullable = false)
    private Integer pullingCnt;

    @Column(nullable = false)
    private Character dealState;

    @OneToMany(mappedBy = "dealPost")
    List<DealPostImage> dealPostImages = new ArrayList<>();

    @OneToOne(mappedBy = "dealPost")
    private DealSuccess dealSuccess;

    @Builder
    public DealPost(User user,Category category,String title,Integer price,String content,Character dealState){
        this.user=user;
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
    }
}
