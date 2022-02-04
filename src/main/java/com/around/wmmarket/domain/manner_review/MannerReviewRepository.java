package com.around.wmmarket.domain.manner_review;

import com.around.wmmarket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MannerReviewRepository extends JpaRepository<MannerReview, Integer>{
    Optional<MannerReview> findBySellerAndBuyerAndManner(User seller, User buyer, Manner manner);
}
