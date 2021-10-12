package com.around.wmmarket.domain.user_like;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLikeId implements Serializable {
    private Integer userId;
    private Integer dealPostId;
}
