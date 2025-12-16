package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.repository.BotSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Helper service to manage BotSession (state) for users.
 */
@Service
@Transactional
public class BotSessionManager {

    private final BotSessionRepository botSessionRepository;

    public BotSessionManager(BotSessionRepository botSessionRepository) {
        this.botSessionRepository = botSessionRepository;
    }

    /**
     * Get existing session or create a new one.
     */
    public BotSession getSession(String waPhoneNumber) {
        return botSessionRepository.findOne((root, query, cb) -> cb.equal(root.get("waPhoneNumber"), waPhoneNumber))
                .orElseGet(() -> {
                    BotSession newSession = new BotSession();
                    newSession.setWaPhoneNumber(waPhoneNumber);
                    newSession.setCurrentState("NEW");
                    newSession.setLastActiveAt(Instant.now());
                    return botSessionRepository.save(newSession);
                });
    }

    public void updateState(BotSession session, String newState) {
        session.setCurrentState(newState);
        session.setLastActiveAt(Instant.now());
        botSessionRepository.save(session);
    }

    public void updateSessionData(BotSession session, String data) {
        session.setSessionData(data);
        session.setLastActiveAt(Instant.now());
        botSessionRepository.save(session);
    }

    // Helper methods for JSON data
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public void setSessionData(BotSession session, String key, Object value) {
        try {
            java.util.Map<String, Object> data = getSessionDataMap(session);
            data.put(key, value);
            session.setSessionData(objectMapper.writeValueAsString(data));
            updateSessionData(session, session.getSessionData());
        } catch (Exception e) {
            // log.error("Error writing session data", e);
        }
    }

    public Object getSessionData(BotSession session, String key) {
        java.util.Map<String, Object> data = getSessionDataMap(session);
        return data.get(key);
    }

    public String getSessionDataString(BotSession session, String key) {
        Object val = getSessionData(session, key);
        return val != null ? String.valueOf(val) : null;
    }

    public Double getSessionDataDouble(BotSession session, String key) {
        Object val = getSessionData(session, key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        } else if (val instanceof String) {
            try {
                return Double.parseDouble((String) val);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, Object> getSessionDataMap(BotSession session) {
        if (session.getSessionData() == null || session.getSessionData().isEmpty()) {
            return new java.util.HashMap<>();
        }
        try {
            return objectMapper.readValue(session.getSessionData(), java.util.Map.class);
        } catch (Exception e) {
            return new java.util.HashMap<>();
        }
    }
}
