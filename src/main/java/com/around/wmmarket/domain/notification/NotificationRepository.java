package com.around.wmmarket.domain.notification;

import com.around.wmmarket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByReceiver(User receiver);
}