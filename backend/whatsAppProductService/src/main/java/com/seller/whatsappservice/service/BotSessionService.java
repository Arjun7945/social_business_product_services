package com.seller.whatsappservice.service;

import com.seller.whatsappservice.model.BotSession;
import com.seller.whatsappservice.model.enums.FlowType;
import com.seller.whatsappservice.repository.BotSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotSessionService {

    private final BotSessionRepository sessionRepository;

    @Transactional
    public BotSession getOrCreateSession(String waPhoneNumber, FlowType flowType) {
        return sessionRepository.findByWaPhoneNumber(waPhoneNumber)
                .orElseGet(() -> {
                    BotSession session = BotSession.builder()
                            .waPhoneNumber(waPhoneNumber)
                            .flowType(flowType)
                            .build();
                    return sessionRepository.save(session);
                });
    }

    @Transactional
    public void setAttribute(String waPhoneNumber, String key, String value) {
        BotSession session = sessionRepository.findByWaPhoneNumber(waPhoneNumber)
                .orElseThrow(() -> new IllegalStateException("Session not found for " + waPhoneNumber));

        session.getAttributes().put(key, value);
        sessionRepository.save(session);
    }

    @Transactional
    public String getAttribute(String waPhoneNumber, String key) {
        BotSession session = sessionRepository.findByWaPhoneNumber(waPhoneNumber)
                .orElse(null);

        if (session == null)
            return null;
        return session.getAttributes().get(key);
    }

    @Transactional
    public void removeAttribute(String waPhoneNumber, String key) {
        sessionRepository.findByWaPhoneNumber(waPhoneNumber)
                .ifPresent(session -> {
                    session.getAttributes().remove(key);
                    sessionRepository.save(session);
                });
    }

    @Transactional
    public void clearSession(String waPhoneNumber) {
        sessionRepository.findByWaPhoneNumber(waPhoneNumber)
                .ifPresent(session -> {
                    session.getAttributes().clear();
                    sessionRepository.save(session);
                });
    }
}
