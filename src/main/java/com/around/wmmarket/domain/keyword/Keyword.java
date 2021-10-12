package com.around.wmmarket.domain.keyword;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "keyword")
@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String word;

    @Builder
    public Keyword(Integer userId, String word){
        this.userId=userId;
        this.word=word;
    }
}
