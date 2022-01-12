package com.around.wmmarket.domain.deal_post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealPostRepository extends JpaRepository<DealPost, Integer> {
    Slice<DealPost> findByDealState(DealState dealState, Pageable pageable);
}
