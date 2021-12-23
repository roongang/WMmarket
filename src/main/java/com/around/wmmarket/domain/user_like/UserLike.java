package com.around.wmmarket.domain.user_like;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_like")
@Entity
public class UserLike extends BaseTimeEntity implements Serializable{
    @EmbeddedId
    private UserLikeId userLikeId=new UserLikeId();

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @MapsId("dealPostId")
    @ManyToOne
    @JoinColumn(name = "DEAL_POST_ID")
    private DealPost dealPost;

    // constructor
    @Builder
    public UserLike(User user,DealPost dealPost){
        setUser(user);
        setDealPost(dealPost);
    }

    // setter
    public void setUser(User user){
        if(this.user!=null) this.user.getUserLikes().remove(this);
        this.user=user;
        user.getUserLikes().add(this);
    }
    public void setDealPost(DealPost dealPost){
        if(this.dealPost!=null) this.dealPost.getUserLikes().remove(this);
        this.dealPost=dealPost;
        dealPost.getUserLikes().add(this);
    }
    // delete
    public void deleteRelation(){
        if(this.user!=null) this.user.getUserLikes().remove(this);
        if(this.dealPost!=null) this.dealPost.getUserLikes().remove(this);
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof UserLike)) return false;
        UserLike userLike=(UserLike) o;
        return this.getUserLikeId().equals(userLike.getUserLikeId());
    }

    @Override
    public int hashCode(){
        return this.getUserLikeId().hashCode()*31;
    }
}
