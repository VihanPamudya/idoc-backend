package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {

}
