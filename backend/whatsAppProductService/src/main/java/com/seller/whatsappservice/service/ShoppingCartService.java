package com.seller.whatsappservice.service;

import com.seller.whatsappservice.dto.CartItemDto;
import com.seller.whatsappservice.model.CartItem;
import com.seller.whatsappservice.model.FishProduct;
import com.seller.whatsappservice.model.ShoppingCart;
import com.seller.whatsappservice.repository.CartItemRepository;
import com.seller.whatsappservice.repository.FishProductRepository;
import com.seller.whatsappservice.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing customer shopping carts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final FishProductRepository fishProductRepository;

    /**
     * Get or create cart for customer
     */
    @Transactional
    public ShoppingCart getOrCreateCart(Long customerId) {
        return shoppingCartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    ShoppingCart cart = ShoppingCart.builder()
                            .customerId(customerId)
                            .items(new ArrayList<>())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    ShoppingCart savedCart = shoppingCartRepository.save(cart);
                    log.info("Created new shopping cart for customer {}", customerId);
                    return savedCart;
                });
    }

    /**
     * Add item to cart
     */
    @Transactional
    public void addToCart(Long customerId, Long fishProductId, Double quantityKg) {
        ShoppingCart cart = getOrCreateCart(customerId);

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository
                .findByCartIdAndFishProductId(cart.getId(), fishProductId)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantityKg(existingItem.getQuantityKg() + quantityKg);
            cartItemRepository.save(existingItem);
            log.info("Updated quantity for product {} in cart. New quantity: {} kg",
                    fishProductId, existingItem.getQuantityKg());
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .fishProductId(fishProductId)
                    .quantityKg(quantityKg)
                    .addedAt(LocalDateTime.now())
                    .build();
            cartItemRepository.save(newItem);
            log.info("Added product {} to cart with quantity {} kg", fishProductId, quantityKg);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public void removeFromCart(Long customerId, Long fishProductId) {
        ShoppingCart cart = shoppingCartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for customer " + customerId));

        cartItemRepository.deleteByCartIdAndFishProductId(cart.getId(), fishProductId);
        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);

        log.info("Removed product {} from cart for customer {}", fishProductId, customerId);
    }

    /**
     * Update quantity of an existing cart item
     */
    @Transactional
    public void updateQuantity(Long customerId, Long fishProductId, Double newQuantity) {
        ShoppingCart cart = shoppingCartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for customer " + customerId));

        CartItem item = cartItemRepository.findByCartIdAndFishProductId(cart.getId(), fishProductId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantityKg(newQuantity);
        cartItemRepository.save(item);

        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);

        log.info("Updated quantity for product {} in cart to {} kg", fishProductId, newQuantity);
    }

    /**
     * Get cart items with product details
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItems(Long customerId) {
        ShoppingCart cart = shoppingCartRepository.findByCustomerId(customerId)
                .orElse(null);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return new ArrayList<>();
        }

        return cart.getItems().stream()
                .map(item -> {
                    FishProduct product = fishProductRepository.findById(item.getFishProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + item.getFishProductId()));

                    double subtotal = item.getQuantityKg() * product.getPricePerKg();

                    return CartItemDto.builder()
                            .fishProductId(product.getId())
                            .fishName(product.getName())
                            .quantityKg(item.getQuantityKg())
                            .pricePerKg(product.getPricePerKg())
                            .subtotal(subtotal)
                            .imageUrl(product.getImageURL())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate cart total
     */
    @Transactional(readOnly = true)
    public Double calculateCartTotal(Long customerId) {
        List<CartItemDto> items = getCartItems(customerId);
        return items.stream()
                .mapToDouble(CartItemDto::getSubtotal)
                .sum();
    }

    /**
     * Clear cart after order placement
     */
    @Transactional
    public void clearCart(Long customerId) {
        shoppingCartRepository.findByCustomerId(customerId)
                .ifPresent(cart -> {
                    shoppingCartRepository.delete(cart);
                    log.info("Cleared cart for customer {}", customerId);
                });
    }

    /**
     * Check if cart is empty
     */
    @Transactional(readOnly = true)
    public boolean isCartEmpty(Long customerId) {
        ShoppingCart cart = shoppingCartRepository.findByCustomerId(customerId)
                .orElse(null);

        return cart == null || cart.getItems() == null || cart.getItems().isEmpty();
    }
}
