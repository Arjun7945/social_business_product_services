package com.aps.domain;

import static com.aps.domain.FishProductTestSamples.*;
import static com.aps.domain.ReturnedOrderItemTestSamples.*;
import static com.aps.domain.ReturnedOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnedOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnedOrderItem.class);
        ReturnedOrderItem returnedOrderItem1 = getReturnedOrderItemSample1();
        ReturnedOrderItem returnedOrderItem2 = new ReturnedOrderItem();
        assertThat(returnedOrderItem1).isNotEqualTo(returnedOrderItem2);

        returnedOrderItem2.setId(returnedOrderItem1.getId());
        assertThat(returnedOrderItem1).isEqualTo(returnedOrderItem2);

        returnedOrderItem2 = getReturnedOrderItemSample2();
        assertThat(returnedOrderItem1).isNotEqualTo(returnedOrderItem2);
    }

    @Test
    void productTest() {
        ReturnedOrderItem returnedOrderItem = getReturnedOrderItemRandomSampleGenerator();
        FishProduct fishProductBack = getFishProductRandomSampleGenerator();

        returnedOrderItem.setProduct(fishProductBack);
        assertThat(returnedOrderItem.getProduct()).isEqualTo(fishProductBack);

        returnedOrderItem.product(null);
        assertThat(returnedOrderItem.getProduct()).isNull();
    }

    @Test
    void returnedOrderTest() {
        ReturnedOrderItem returnedOrderItem = getReturnedOrderItemRandomSampleGenerator();
        ReturnedOrder returnedOrderBack = getReturnedOrderRandomSampleGenerator();

        returnedOrderItem.setReturnedOrder(returnedOrderBack);
        assertThat(returnedOrderItem.getReturnedOrder()).isEqualTo(returnedOrderBack);

        returnedOrderItem.returnedOrder(null);
        assertThat(returnedOrderItem.getReturnedOrder()).isNull();
    }
}
