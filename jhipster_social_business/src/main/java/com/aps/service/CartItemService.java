package com.aps.service;

import com.aps.domain.CartItem;
import com.aps.repository.CartItemRepository;
import com.aps.service.dto.CartItemDTO;
import com.aps.service.mapper.CartItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.CartItem}.
 */
@Service
@Transactional
public class CartItemService {

    private static final Logger LOG = LoggerFactory.getLogger(CartItemService.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    public CartItemService(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    /**
     * Save a cartItem.
     *
     * @param cartItemDTO the entity to save.
     * @return the persisted entity.
     */
    public CartItemDTO save(CartItemDTO cartItemDTO) {
        LOG.debug("Request to save CartItem : {}", cartItemDTO);
        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    /**
     * Update a cartItem.
     *
     * @param cartItemDTO the entity to save.
     * @return the persisted entity.
     */
    public CartItemDTO update(CartItemDTO cartItemDTO) {
        LOG.debug("Request to update CartItem : {}", cartItemDTO);
        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    /**
     * Partially update a cartItem.
     *
     * @param cartItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CartItemDTO> partialUpdate(CartItemDTO cartItemDTO) {
        LOG.debug("Request to partially update CartItem : {}", cartItemDTO);

        return cartItemRepository
            .findById(cartItemDTO.getId())
            .map(existingCartItem -> {
                cartItemMapper.partialUpdate(existingCartItem, cartItemDTO);

                return existingCartItem;
            })
            .map(cartItemRepository::save)
            .map(cartItemMapper::toDto);
    }

    /**
     * Get all the cartItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CartItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cartItemRepository.findAllWithEagerRelationships(pageable).map(cartItemMapper::toDto);
    }

    /**
     * Get one cartItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CartItemDTO> findOne(Long id) {
        LOG.debug("Request to get CartItem : {}", id);
        return cartItemRepository.findOneWithEagerRelationships(id).map(cartItemMapper::toDto);
    }

    /**
     * Delete the cartItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CartItem : {}", id);
        cartItemRepository.deleteById(id);
    }
}
