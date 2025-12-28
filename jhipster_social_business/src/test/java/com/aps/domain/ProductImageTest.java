package com.aps.domain;

import static com.aps.domain.FishProductTestSamples.*;
import static com.aps.domain.ProductImageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductImage.class);
        ProductImage productImage1 = getProductImageSample1();
        ProductImage productImage2 = new ProductImage();
        assertThat(productImage1).isNotEqualTo(productImage2);

        productImage2.setId(productImage1.getId());
        assertThat(productImage1).isEqualTo(productImage2);

        productImage2 = getProductImageSample2();
        assertThat(productImage1).isNotEqualTo(productImage2);
    }

    @Test
    void productTest() {
        ProductImage productImage = getProductImageRandomSampleGenerator();
        FishProduct fishProductBack = getFishProductRandomSampleGenerator();

        productImage.setProduct(fishProductBack);
        assertThat(productImage.getProduct()).isEqualTo(fishProductBack);
        assertThat(fishProductBack.getImage()).isEqualTo(productImage);

        productImage.product(null);
        assertThat(productImage.getProduct()).isNull();
        assertThat(fishProductBack.getImage()).isNull();
    }
}
