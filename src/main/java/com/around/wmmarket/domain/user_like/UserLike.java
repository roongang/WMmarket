package com.around.wmmarket.domain.user_like;

import com.around.wmmarket.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_like")
@Entity
public class UserLike extends BaseTimeEntity implements Serializable {
    @EmbeddedId
    private UserLikeId userLikeId;
}
