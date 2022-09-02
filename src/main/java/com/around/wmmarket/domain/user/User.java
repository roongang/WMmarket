package com.around.wmmarket.domain.user;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_review.DealReview;
import com.around.wmmarket.domain.deal_success.DealSuccess;
import com.around.wmmarket.domain.keyword.Keyword;
import com.around.wmmarket.domain.manner_review.MannerReview;
import com.around.wmmarket.domain.notification.Notification;
import com.around.wmmarket.domain.user_like.UserLike;
import com.around.wmmarket.domain.user_role.UserRole;
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String image;

    @Column(nullable = false, length=100, unique = true)
    private String nickname;

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
    // not yet
    @OneToMany(mappedBy = "receiver")
    private final List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private final List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private final List<MannerReview> sellMannerReviews = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private final List<MannerReview> buyMannerReviews = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private final List<DealReview> sellDealReviews = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private final List<DealReview> buyDealReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private final List<DealPost> dealPosts = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private final List<DealSuccess> dealSuccesses = new ArrayList<>();

    // 부모가 사라지면 자식(userLike)도 같이 사라짐
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private final List<UserLike> userLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private final List<UserRole> userRoles = new ArrayList<>();

    @Builder
    public User(String email,String password,String image,String nickname,String city_1,String town_1,String city_2,String town_2,int isAuth,String code){
        this.email=email;
        this.password=password;
        this.image=image;
        this.nickname=nickname;
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
    public void setCity_1(String city_1){this.city_1=city_1;}
    public void setTown_1(String town_1){this.town_1=town_1;}
    public void setCity_2(String city_2){this.city_2=city_2;}
    public void setTown_2(String town_2){this.town_2=town_2;}
    public void setCode(String code){this.code=code;}
    public void setIsAuth(Integer isAuth){this.isAuth=isAuth;}
    // delete
    @PreRemove
    public void makeChildNull(){
        // not yet
        // make child fk null
        while(!this.sellDealReviews.isEmpty()) this.sellDealReviews.get(this.sellDealReviews.size()-1).setSeller(null);
        while(!this.buyDealReviews.isEmpty()) this.buyDealReviews.get(this.buyDealReviews.size()-1).setBuyer(null);
        while(!this.dealPosts.isEmpty()) this.dealPosts.get(this.dealPosts.size()-1).setUser(null);
        while(!this.dealSuccesses.isEmpty()) this.dealSuccesses.get(this.dealSuccesses.size()-1).setBuyer(null);
        while(!this.sellMannerReviews.isEmpty()) this.sellMannerReviews.get(this.sellDealReviews.size()-1).setSeller(null);
        while(!this.buyMannerReviews.isEmpty()) this.buyMannerReviews.get(this.buyMannerReviews.size()-1).setBuyer(null);
        while(!this.keywords.isEmpty()) this.keywords.get(this.keywords.size()-1).setUser(null);
        while(!this.userLikes.isEmpty()) this.userLikes.get(this.userLikes.size()-1).setUser(null);
        while(!this.userRoles.isEmpty()) this.userRoles.get(this.userRoles.size()-1).setUser(null);
    }
}