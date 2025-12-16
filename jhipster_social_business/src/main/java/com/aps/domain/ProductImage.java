package com.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Images associated with a product.
 */
@Entity
@Table(name = "product_image")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @NotNull
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "images" }, allowSetters = true)
    private FishProduct product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "mime_type")
    private String mimeType;

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getId() {
        return this.id;
    }

    public ProductImage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ProductImage imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public ProductImage displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public FishProduct getProduct() {
        return this.product;
    }

    public void setProduct(FishProduct fishProduct) {
        this.product = fishProduct;
    }

    public ProductImage product(FishProduct fishProduct) {
        this.setProduct(fishProduct);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductImage)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductImage) o).getId());
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
        return "ProductImage{" +
                "id=" + getId() +
                ", imageUrl='" + getImageUrl() + "'" +
                ", displayOrder=" + getDisplayOrder() +
                "}";
    }
}
