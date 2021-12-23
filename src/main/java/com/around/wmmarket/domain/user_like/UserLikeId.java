package com.around.wmmarket.domain.user_like;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class UserLikeId implements Serializable {
    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "DEAL_POST_ID")
    private Integer dealPostId;

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof UserLikeId)) return false;
        UserLikeId userLikeId = (UserLikeId) o;
        return ((this.dealPostId.equals(userLikeId.dealPostId))&&(this.userId.equals(userLikeId.userId)));
    }

    @Override
    public int hashCode(){
        return this.dealPostId.hashCode()*31+this.userId.hashCode();
    }
}
