package com.around.wmmarket.domain.user_role;

import com.around.wmmarket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {
    void deleteByUser(User user);
}
