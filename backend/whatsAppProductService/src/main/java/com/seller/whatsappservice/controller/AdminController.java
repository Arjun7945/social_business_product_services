package com.seller.whatsappservice.controller;

import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.FishProduct;
import com.seller.whatsappservice.repository.TeamMemberRepository;
import com.seller.whatsappservice.repository.FishProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final FishProductRepository fishProductRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final com.seller.whatsappservice.repository.CustomerOrderRepository customerOrderRepository;
    private final com.seller.whatsappservice.repository.CustomerRepository customerRepository;

    // --- Fish Product Management ---

    @GetMapping("/fish")
    public List<FishProduct> getAllFish() {
        return fishProductRepository.findAll();
    }

    @PostMapping("/fish")
    public FishProduct createFish(@RequestBody FishProduct fishProduct) {
        log.info("Creating fish product: name={}, pricePerKg={}, isAvailable={}",
                fishProduct.getName(), fishProduct.getPricePerKg(), fishProduct.isAvailable());
        FishProduct saved = fishProductRepository.save(fishProduct);
        log.info("Saved fish product with id={}, isAvailable={}", saved.getId(), saved.isAvailable());
        return saved;
    }

    @PutMapping("/fish/{id}")
    public ResponseEntity<FishProduct> updateFish(@PathVariable Long id, @RequestBody FishProduct fishDetails) {
        log.info("Updating fish product id={}: name={}, pricePerKg={}, isAvailable={}",
                id, fishDetails.getName(), fishDetails.getPricePerKg(), fishDetails.isAvailable());
        return fishProductRepository.findById(id)
                .map(fish -> {
                    fish.setName(fishDetails.getName());
                    fish.setPricePerKg(fishDetails.getPricePerKg());
                    fish.setImageURL(fishDetails.getImageURL());
                    fish.setDescription(fishDetails.getDescription());
                    fish.setAvailable(fishDetails.isAvailable());
                    FishProduct updated = fishProductRepository.save(fish);
                    log.info("Updated fish product id={}, isAvailable={}", updated.getId(), updated.isAvailable());
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/fish/{id}")
    public ResponseEntity<Void> deleteFish(@PathVariable Long id) {
        if (fishProductRepository.existsById(id)) {
            fishProductRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Team Member Management ---

    @GetMapping("/team-members")
    public List<TeamMember> getAllTeamMembers() {
        return teamMemberRepository.findAll();
    }

    @PostMapping("/team-members")
    public TeamMember createTeamMember(@RequestBody TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    @PutMapping("/team-members/{id}")
    public ResponseEntity<TeamMember> updateTeamMember(@PathVariable Long id,
            @RequestBody TeamMember details) {
        return teamMemberRepository.findById(id)
                .map(person -> {
                    person.setName(details.getName());
                    person.setPhoneNumber(details.getPhoneNumber());
                    person.setActive(details.isActive());
                    person.setRole(details.getRole());
                    return ResponseEntity.ok(teamMemberRepository.save(person));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/team-members/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        if (teamMemberRepository.existsById(id)) {
            teamMemberRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Order Management ---

    @GetMapping("/orders")
    public List<com.seller.whatsappservice.dto.CustomerOrderDto> getAllOrders() {
        return customerOrderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<com.seller.whatsappservice.dto.CustomerOrderDto> getOrderById(@PathVariable Long id) {
        return customerOrderRepository.findById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private com.seller.whatsappservice.dto.CustomerOrderDto mapToDto(
            com.seller.whatsappservice.model.CustomerOrder order) {
        String customerName = "Unknown Customer";
        try {
            customerName = customerRepository.findById(order.getCustomerId())
                    .map(com.seller.whatsappservice.model.Customer::getName)
                    .orElse("Unknown Customer");
        } catch (Exception e) {
            log.error("Error fetching customer name for order {}", order.getId(), e);
        }

        List<com.seller.whatsappservice.dto.OrderItemDto> items = order.getItems().stream()
                .map(item -> {
                    String fishName = "Unknown Fish";
                    try {
                        fishName = fishProductRepository.findById(item.getFishProductId())
                                .map(FishProduct::getName)
                                .orElse("Unknown Fish");
                    } catch (Exception e) {
                        log.error("Error fetching fish name for item {}", item.getId(), e);
                    }

                    return com.seller.whatsappservice.dto.OrderItemDto.builder()
                            .id(item.getId())
                            .fishProductId(item.getFishProductId())
                            .fishProductName(fishName)
                            .quantity(item.getQuantityKg())
                            .pricePerKg(item.getPriceAtOrder())
                            .totalPrice(item.getQuantityKg() * item.getPriceAtOrder())
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());

        return com.seller.whatsappservice.dto.CustomerOrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .customerName(customerName)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .teamMemberId(order.getTeamMemberId())
                .items(items)
                .build();
    }
}
