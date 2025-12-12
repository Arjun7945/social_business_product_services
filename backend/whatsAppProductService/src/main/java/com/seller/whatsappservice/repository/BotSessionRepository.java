package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.BotSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotSessionRepository extends JpaRepository<BotSession, Long> {
    Optional<BotSession> findByWaPhoneNumber(String waPhoneNumber);
}
