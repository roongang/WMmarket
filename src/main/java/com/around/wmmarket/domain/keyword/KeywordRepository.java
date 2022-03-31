package com.around.wmmarket.domain.keyword;

import com.around.wmmarket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword,Integer> {
    Optional<Keyword> findByUserAndWord(User user,String word);
}
