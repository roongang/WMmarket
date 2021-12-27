package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user_like.UserLike;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    private DealState dealState;

    @OneToMany(mappedBy = "dealPost")
    private List<DealPostImage> dealPostImages = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime pullingDate;

    @Column(nullable = false)
    private Integer pullingCnt;

    @OneToOne(mappedBy = "dealPost")
    private DealSuccess dealSuccess;

    @OneToMany(mappedBy = "dealPost")
    private List<DealReview> dealReviews = new ArrayList<>();

    @OneToMany(mappedBy = "dealPost")
    private List<UserLike> userLikes = new ArrayList<>();

    @Builder
    public DealPost(User user,Category category,String title,Integer price,String content,DealState dealState){
        setUser(user);
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
    }
    
    // 영속화전 전처리
    @PrePersist
    public void prePersist(){
        this.pullingCnt=(this.pullingCnt==null)?0:this.pullingCnt;
        this.pullingDate=(this.pullingDate==null)?LocalDateTime.now():this.pullingDate;
    }

    // setter
    public void setUser(User user){
        // 기존 관계 제거
        if(this.user!=null) this.user.getDealPosts().remove(this);
        this.user=user;
        if(user!=null) user.getDealPosts().add(this);
    }
    public void setCategory(Category category){this.category=category;}
    public void setTitle(String title){this.title=title;}
    public void setPrice(Integer price){this.price=price;}
    public void setContent(String content){this.content=content;}
    public void setDealState(DealState dealState){this.dealState=dealState;}
    public void setDealSuccess(DealSuccess dealSuccess){this.dealSuccess=dealSuccess;}
    // delete
    public void deleteRelation(){
        if(this.user!=null) this.user.getDealPosts().remove(this);
    }
}
