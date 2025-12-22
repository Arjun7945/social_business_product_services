package com.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Product catalog entity.
 * Indexed in Elasticsearch for fast search.
 */
@Entity
@Table(name = "fish_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Increased length for SEO descriptions
     */
    @Size(max = 2000)
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * One Product has many Images
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductImage> images = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FishProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public FishProduct name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPricePerKg() {
        return this.pricePerKg;
    }

    public FishProduct pricePerKg(BigDecimal pricePerKg) {
        this.setPricePerKg(pricePerKg);
        return this;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public FishProduct imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public FishProduct description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public FishProduct isAvailable(Boolean isAvailable) {
        this.setIsAvailable(isAvailable);
        return this;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public FishProduct createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<ProductImage> getImages() {
        return this.images;
    }

    public void setImages(Set<ProductImage> productImages) {
        if (this.images != null) {
            this.images.forEach(i -> i.setProduct(null));
        }
        if (productImages != null) {
            productImages.forEach(i -> i.setProduct(this));
        }
        this.images = productImages;
    }

    public FishProduct images(Set<ProductImage> productImages) {
        this.setImages(productImages);
        return this;
    }

    public FishProduct addImages(ProductImage productImage) {
        this.images.add(productImage);
        productImage.setProduct(this);
        return this;
    }

    public FishProduct removeImages(ProductImage productImage) {
        this.images.remove(productImage);
        productImage.setProduct(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FishProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((FishProduct) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FishProduct{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", pricePerKg=" + getPricePerKg() +
                ", imageUrl='" + getImageUrl() + "'" +
                ", description='" + getDescription() + "'" +
                ", isAvailable='" + getIsAvailable() + "'" +
                ", createdAt='" + getCreatedAt() + "'" +
                "}";
    }
}
