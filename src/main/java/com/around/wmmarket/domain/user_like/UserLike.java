package com.around.wmmarket.domain.user_like;

import com.around.wmmarket.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Table(name = "user_like")
@Entity
@IdClass(UserLikeId.class)
public class UserLike extends BaseTimeEntity implements Serializable {

    @Id
    private Integer userId;

    @Id
    private Integer dealPostId;

}
