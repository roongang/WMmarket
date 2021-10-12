package com.around.wmmarket.domain.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Integer> {
}
