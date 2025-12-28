package com.aps.domain;

import static com.aps.domain.RemovedOrderSummaryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RemovedOrderSummaryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RemovedOrderSummary.class);
        RemovedOrderSummary removedOrderSummary1 = getRemovedOrderSummarySample1();
        RemovedOrderSummary removedOrderSummary2 = new RemovedOrderSummary();
        assertThat(removedOrderSummary1).isNotEqualTo(removedOrderSummary2);

        removedOrderSummary2.setId(removedOrderSummary1.getId());
        assertThat(removedOrderSummary1).isEqualTo(removedOrderSummary2);

        removedOrderSummary2 = getRemovedOrderSummarySample2();
        assertThat(removedOrderSummary1).isNotEqualTo(removedOrderSummary2);
    }
}
