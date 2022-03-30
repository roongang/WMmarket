package com.around.wmmarket.domain.keyword;

import com.around.wmmarket.domain.BaseTimeEntity;
import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keyword")
@Entity
public class Keyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false)
    private String word;

    @Builder
    public Keyword(User user,String word){
        setUser(user);
        this.word=word;
    }

    public void setUser(User user) {
        if(this.user!=null) this.user.getKeywords().remove(this);
        this.user=user;
        if(user!=null) user.getKeywords().add(this);
    }
    @PreRemove
    public void deleteRelation(){
        if(this.user!=null) this.user.getKeywords().remove(this);
    }
}
