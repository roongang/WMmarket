package com.around.wmmarket.domain.user;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.keyword.Keyword;
import com.around.wmmarket.domain.manner_review.MannerReview;
import com.around.wmmarket.domain.notification.Notification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String image;

    @Column(nullable = false, length=100)
    private String nickname;

    // TODO : 권한도 List 로 가져야함
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=50)
    private Role role;

    @Column(nullable = true)
    private String city_1;

    @Column(nullable = true)
    private String town_1;

    @Column(nullable = true)
    private String city_2;

    @Column(nullable = true)
    private String town_2;

    @Column(nullable = true)
    private Integer isAuth;

    @Column(nullable = true)
    private String code;

    // 관계 매핑
    @OneToMany(mappedBy = "user")
    List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    List<MannerReview> mannerReviews = new ArrayList<>();

    @OneToMany(mappedBy = "dealPost")
    List<DealReview> dealReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    List<DealPost> dealPosts = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    List<DealSuccess> dealSuccesses = new ArrayList<>();

    @Builder
    public User(String email,String password,String image,String nickname,Role role,String city_1,String town_1,String city_2,String town_2,int isAuth,String code){
        this.email=email;
        this.password=password;
        this.image=image;
        this.nickname=nickname;
        this.role=role;
        this.city_1=city_1;
        this.town_1=town_1;
        this.city_2=city_2;
        this.town_2=town_2;
        this.isAuth=isAuth;
        this.code=code;
    }

    // setter
    public void setPassword(String password){this.password=password;}
    public void setImage(String image){this.image=image;}
    public void setNickname(String nickname){this.nickname=nickname;}
    public void setRole(Role role){this.role=role;}
    public void setCity_1(String city_1){this.city_1=city_1;}
    public void setTown_1(String town_1){this.town_1=town_1;}
    public void setCity_2(String city_2){this.city_2=city_2;}
    public void setTown_2(String town_2){this.town_2=town_2;}
}