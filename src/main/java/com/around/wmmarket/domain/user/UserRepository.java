package com.around.wmmarket.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);

    @EntityGraph(attributePaths = {"userRoles"})
    Optional<User> findWithUserRolesByEmail(String email);

    @EntityGraph(attributePaths = {"userLikes"})
    Optional<User> findWithUserLikesByEmail(String email);

    @EntityGraph(attributePaths = {"dealPosts"})
    Optional<User> findWithDealPostsByEmail(String email);

    @EntityGraph(attributePaths = {"dealSuccesses"})
    Optional<User> findWithDealSuccessesByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}