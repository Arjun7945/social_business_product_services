package com.aps.domain;

import static com.aps.domain.FishProductTestSamples.*;
import static com.aps.domain.ProductImageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FishProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FishProduct.class);
        FishProduct fishProduct1 = getFishProductSample1();
        FishProduct fishProduct2 = new FishProduct();
        assertThat(fishProduct1).isNotEqualTo(fishProduct2);

        fishProduct2.setId(fishProduct1.getId());
        assertThat(fishProduct1).isEqualTo(fishProduct2);

        fishProduct2 = getFishProductSample2();
        assertThat(fishProduct1).isNotEqualTo(fishProduct2);
    }

    @Test
    void imagesTest() {
        FishProduct fishProduct = getFishProductRandomSampleGenerator();
        ProductImage productImageBack = getProductImageRandomSampleGenerator();

        fishProduct.addImages(productImageBack);
        assertThat(fishProduct.getImages()).containsOnly(productImageBack);
        assertThat(productImageBack.getProduct()).isEqualTo(fishProduct);

        fishProduct.removeImages(productImageBack);
        assertThat(fishProduct.getImages()).doesNotContain(productImageBack);
        assertThat(productImageBack.getProduct()).isNull();

        fishProduct.images(new HashSet<>(Set.of(productImageBack)));
        assertThat(fishProduct.getImages()).containsOnly(productImageBack);
        assertThat(productImageBack.getProduct()).isEqualTo(fishProduct);

        fishProduct.setImages(new HashSet<>());
        assertThat(fishProduct.getImages()).doesNotContain(productImageBack);
        assertThat(productImageBack.getProduct()).isNull();
    }
}
