package com.aps.service.customer_flow;

import com.aps.domain.BotSession;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.service.BotSessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Manages the flow state and session data for the customer conversation.
 * Extracted from CustomerFlowService.
 */
@Service
public class FlowStateService {

    private final Logger log = LoggerFactory.getLogger(FlowStateService.class);

    private final BotSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    public FlowStateService(BotSessionManager sessionManager, ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    public CustomerFlowStage getStage(BotSession session) {
        try {
            return CustomerFlowStage.valueOf(session.getCurrentState());
        } catch (IllegalArgumentException | NullPointerException e) {
            return CustomerFlowStage.NEW;
        }
    }

    public void updateStage(BotSession session, CustomerFlowStage stage) {
        sessionManager.updateState(session, stage.name());
    }

    public boolean isActiveSession(CustomerFlowStage stage) {
        return (stage == CustomerFlowStage.BROWSING ||
                stage == CustomerFlowStage.ADDING_TO_CART ||
                stage == CustomerFlowStage.AWAITING_QUANTITY ||
                stage == CustomerFlowStage.CHECKOUT ||
                stage == CustomerFlowStage.CONFIRMING_ORDER ||
                stage == CustomerFlowStage.EDITING_ORDER ||
                stage == CustomerFlowStage.EDITING_PRODUCT ||
                stage == CustomerFlowStage.EDITING_QUANTITY);
    }

    public void setSessionData(BotSession session, String key, Object value) {
        try {
            Map<String, Object> data = getSessionDataMap(session);
            data.put(key, value);
            session.setSessionData(objectMapper.writeValueAsString(data));
            sessionManager.updateSessionData(session, session.getSessionData());
        } catch (JsonProcessingException e) {
            log.error("Error writing session data", e);
        }
    }

    public Long getSessionDataLong(BotSession session, String key) {
        Map<String, Object> data = getSessionDataMap(session);
        Object val = data.get(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return null;
    }

    public String getSessionDataString(BotSession session, String key) {
        Map<String, Object> data = getSessionDataMap(session);
        Object val = data.get(key);
        if (val instanceof String) {
            return (String) val;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getSessionDataMap(BotSession session) {
        if (session.getSessionData() == null || session.getSessionData().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(session.getSessionData(), Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error reading session data", e);
            return new HashMap<>();
        }
    }
}
