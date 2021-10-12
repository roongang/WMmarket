package com.around.wmmarket.domain.user_residence;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "user_residence")
@Entity
public class UserResidence {

    @Id
    private Integer userId;

    @Column(nullable = false)
    private String city_1;

    @Column(nullable = false)
    private String town_1;

    @Column(nullable = false)
    private String city_2;

    @Column(nullable = false)
    private String town_2;

    @Builder
    public UserResidence(String city_1, String town_1, String city_2, String town_2){
        this.city_1=city_1;
        this.town_1=town_1;
        this.city_2=city_2;
        this.town_2=town_2;
    }
}