package com.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Product catalog entity.
 * Indexed in Elasticsearch for fast search.
 * UPDATED: 'imageUrl' removed in favor of ProductImage entity relation.
 */
@Entity
@Table(name = "fish_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FishProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price_per_kg", precision = 21, scale = 2, nullable = false)
    private BigDecimal pricePerKg;

    /**
     * To track available stock
     */
    @Column(name = "available_quantity")
    private Double availableQuantity;

    @Size(max = 2000)
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * One Product has exactly One Image
     */
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private ProductImage image;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public FishProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public FishProduct name(String name) {
        this.setName(name);
        return this;
    }

    public FishProduct pricePerKg(BigDecimal pricePerKg) {
        this.setPricePerKg(pricePerKg);
        return this;
    }

    public FishProduct availableQuantity(Double availableQuantity) {
        this.setAvailableQuantity(availableQuantity);
        return this;
    }

    public FishProduct description(String description) {
        this.setDescription(description);
        return this;
    }

    public FishProduct isAvailable(Boolean isAvailable) {
        this.setIsAvailable(isAvailable);
        return this;
    }

    public FishProduct createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public FishProduct image(ProductImage productImage) {
        this.setImage(productImage);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here
}
