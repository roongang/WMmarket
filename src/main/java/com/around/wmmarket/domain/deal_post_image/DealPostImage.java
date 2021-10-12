package com.around.wmmarket.domain.deal_post_image;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "deal_post_image")
@Entity
public class DealPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private Integer dealId;

    @Builder
    public DealPostImage(String path,Integer dealId){
        this.path=path;
        this.dealId=dealId;
    }
}