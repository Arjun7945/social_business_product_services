package com.aps.web.rest;

import com.aps.service.OrderStatusHistoryService;
import com.aps.service.dto.OrderStatusHistoryDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link com.aps.domain.OrderStatusHistory}.
 */
@RestController
@RequestMapping("/api")
public class OrderStatusHistoryResource {

    private final Logger log = LoggerFactory.getLogger(OrderStatusHistoryResource.class);

    private final OrderStatusHistoryService orderStatusHistoryService;

    public OrderStatusHistoryResource(OrderStatusHistoryService orderStatusHistoryService) {
        this.orderStatusHistoryService = orderStatusHistoryService;
    }

    /**
     * {@code GET  /order-status-histories/order/:orderId} : get all the history for
     * a specific order.
     *
     * @param orderId the id of the order.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of orderStatusHistories.
     */
    @GetMapping("/order-status-histories/order/{orderId}")
    public List<OrderStatusHistoryDTO> getHistoryByOrder(@PathVariable Long orderId) {
        log.debug("REST request to get OrderStatusHistory for order : {}", orderId);
        return orderStatusHistoryService.findAllByOrderId(orderId);
    }
}
