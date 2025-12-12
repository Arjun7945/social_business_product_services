package com.seller.whatsappservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Comparator;
import java.util.stream.Collectors;

@Entity
@Table(name = "fish_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FishProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double pricePerKg;

    @Column(name = "image_url")
    private String imageURL;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @JsonProperty("isAvailable")
    private boolean isAvailable;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<ProductImage> images = new java.util.ArrayList<>();

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public java.util.List<ProductImage> getOrderedImages() {
        return images.stream()
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder))
                .collect(Collectors.toList());
    }
}
