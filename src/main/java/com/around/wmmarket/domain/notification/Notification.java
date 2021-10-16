package com.around.wmmarket.domain.notification;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
@Entity
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String type;

    @Builder
    public Notification(User user,String content,String type){
        this.user=user;
        this.content=content;
        this.type=type;
    }
}
