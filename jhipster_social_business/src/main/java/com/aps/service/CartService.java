package com.aps.service;

import com.aps.domain.CartItem;
import com.aps.domain.Customer;
import com.aps.domain.FishProduct;
import com.aps.domain.ShoppingCart;
import com.aps.repository.CartItemRepository;
import com.aps.repository.CustomerRepository;
import com.aps.repository.FishProductRepository;
import com.aps.repository.ShoppingCartRepository;
import com.aps.service.dto.CartItemDetailsDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for business logic related to Shopping Carts.
 * Separated from JHipster's CRUD ShoppingCartService.
 */
@Service
@Transactional
public class CartService {

    private final Logger log = LoggerFactory.getLogger(CartService.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final FishProductRepository fishProductRepository;
    private final CustomerRepository customerRepository;

    public CartService(
            ShoppingCartRepository shoppingCartRepository,
            CartItemRepository cartItemRepository,
            FishProductRepository fishProductRepository,
            CustomerRepository customerRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.fishProductRepository = fishProductRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Get or create cart for customer
     */
    public ShoppingCart getOrCreateCart(Long customerId) {
        return shoppingCartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new RuntimeException("Customer not found"));

                    ShoppingCart cart = new ShoppingCart();
                    cart.setCustomer(customer);
                    cart.setCreatedAt(Instant.now());
                    cart.setUpdatedAt(Instant.now());

                    ShoppingCart savedCart = shoppingCartRepository.save(cart);
                    log.info("Created new shopping cart for customer {}", customerId);
                    return savedCart;
                });
    }

    /**
     * Add item to cart
     */
    public void addToCart(Long customerId, Long fishProductId, Double quantityKg) {
        ShoppingCart cart = getOrCreateCart(customerId);

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), fishProductId).orElse(null);

        if (existingItem != null) {
            existingItem.setQuantityKg(existingItem.getQuantityKg() + quantityKg);
            cartItemRepository.save(existingItem);
            log.info("Updated quantity for product {} in cart. New quantity: {}", fishProductId,
                    existingItem.getQuantityKg());
        } else {
            FishProduct product = fishProductRepository
                    .findById(fishProductId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantityKg(quantityKg);

            cartItemRepository.save(newItem);
            log.info("Added product {} to cart with quantity {} kg", fishProductId, quantityKg);
        }

        cart.setUpdatedAt(Instant.now());
        shoppingCartRepository.save(cart);
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(Long customerId, Long fishProductId) {
        ShoppingCart cart = getOrCreateCart(customerId);

        cartItemRepository
                .findByCartIdAndProductId(cart.getId(), fishProductId)
                .ifPresent(item -> {
                    cartItemRepository.delete(item);
                    log.info("Removed product {} from cart for customer {}", fishProductId, customerId);
                });

        cart.setUpdatedAt(Instant.now());
        shoppingCartRepository.save(cart);
    }

    /**
     * Update quantity of an existing cart item
     */
    public void updateQuantity(Long customerId, Long fishProductId, Double newQuantity) {
        ShoppingCart cart = getOrCreateCart(customerId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), fishProductId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantityKg(newQuantity);
        cartItemRepository.save(item);

        cart.setUpdatedAt(Instant.now());
        shoppingCartRepository.save(cart);

        log.info("Updated quantity for product {} in cart to {} kg", fishProductId, newQuantity);
    }

    /**
     * Get cart items with product details
     */
    @Transactional(readOnly = true)
    public List<CartItemDetailsDTO> getCartItems(Long customerId) {
        ShoppingCart cart = getOrCreateCart(customerId);

        // Use repository directly to avoid EntityNotFoundException from stale OneToMany
        // collection
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        return items
                .stream()
                .map(item -> {
                    FishProduct product = item.getProduct();
                    Double qty = item.getQuantityKg();
                    BigDecimal price = product.getPricePerKg();

                    double subtotal = qty * price.doubleValue();

                    return CartItemDetailsDTO.builder()
                            .fishProductId(product.getId())
                            .fishName(product.getName())
                            .quantityKg(qty)
                            .pricePerKg(price.doubleValue())
                            .subtotal(subtotal)
                            .subtotal(subtotal)
                            .imageUrl(
                                    product.getImage() != null
                                            ? "/api/product-images/public/" + product.getImage().getId() + "/content"
                                            : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate cart total
     */
    @Transactional(readOnly = true)
    public Double calculateCartTotal(Long customerId) {
        List<CartItemDetailsDTO> items = getCartItems(customerId);
        return items.stream().mapToDouble(CartItemDetailsDTO::getSubtotal).sum();
    }

    /**
     * Clear cart after order placement
     */
    public void clearCart(Long customerId) {
        shoppingCartRepository
                .findByCustomerId(customerId)
                .ifPresent(cart -> {
                    cartItemRepository.deleteByCartId(cart.getId());
                    // Clear the collection in memory to avoid stale state in current transaction
                    if (cart.getItems() != null) {
                        cart.getItems().clear();
                    }
                });
    }

    /**
     * Check if cart is empty
     */
    @Transactional(readOnly = true)
    public boolean isCartEmpty(Long customerId) {
        return getCartItems(customerId).isEmpty();
    }
}
