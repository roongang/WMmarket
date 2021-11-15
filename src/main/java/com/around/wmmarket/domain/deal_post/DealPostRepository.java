package com.around.wmmarket.domain.deal_post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DealPostRepository extends JpaRepository<DealPost, Integer> {
    //@Query("SELECT id FROM deal_post ORDER BY id DESC")
    //List<DealPost> findAllDesc();

}
