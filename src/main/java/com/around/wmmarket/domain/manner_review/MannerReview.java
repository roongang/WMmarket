package com.around.wmmarket.domain.manner_review;

import com.around.wmmarket.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "manner_review")
@Entity
public class MannerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "SELLER_ID")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Manner manner;

    @Builder
    public MannerReview(User seller,User buyer,Manner manner){
        this.seller=seller;
        this.buyer=buyer;
        this.manner=manner;
    }

    // delete
    @PreRemove
    public void deleteRelation(){
        if(this.buyer!=null) this.buyer.getBuyMannerReviews().remove(this);
        if(this.seller!=null) this.seller.getSellMannerReviews().remove(this);
    }

    public void setSeller(User seller){
        if(this.seller!=null) this.seller.getSellMannerReviews().remove(this);
        this.seller=seller;
        if(seller!=null) seller.getSellMannerReviews().add(this);
    }

    public void setBuyer(User buyer){
        if(this.buyer!=null) this.buyer.getBuyMannerReviews().remove(this);
        this.buyer=buyer;
        if(buyer!=null) buyer.getBuyMannerReviews().add(this);
    }

}
