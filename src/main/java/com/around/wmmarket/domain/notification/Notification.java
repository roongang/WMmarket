package com.around.wmmarket.domain.notification;

import com.around.wmmarket.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "notification")
@Entity
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String type;

    @Builder
    public Notification(Integer userId,String content,String type){
        this.userId=userId;
        this.content=content;
        this.type=type;
    }
}
