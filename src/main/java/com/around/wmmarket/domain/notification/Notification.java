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
    private User receiver;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Boolean isRead;

    @Builder
    public Notification(User receiver,String content,NotificationType type){
        this.receiver=receiver;
        this.content=content;
        this.type=type;
    }

    @PrePersist
    public void prePersist(){
        isRead=(isRead!=null)?isRead:false;
    }

    public void read(){
        this.isRead=true;
    }
}
