package com.aps.domain;

import static com.aps.domain.FishProductTestSamples.*;
import static com.aps.domain.ProductImageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
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
    void imageTest() {
        FishProduct fishProduct = getFishProductRandomSampleGenerator();
        ProductImage productImageBack = getProductImageRandomSampleGenerator();

        fishProduct.setImage(productImageBack);
        assertThat(fishProduct.getImage()).isEqualTo(productImageBack);

        fishProduct.image(null);
        assertThat(fishProduct.getImage()).isNull();
    }
}
