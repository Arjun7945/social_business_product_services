package com.aps.domain;

import static com.aps.domain.ReturnStatusHistoryTestSamples.*;
import static com.aps.domain.ReturnedOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnStatusHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnStatusHistory.class);
        ReturnStatusHistory returnStatusHistory1 = getReturnStatusHistorySample1();
        ReturnStatusHistory returnStatusHistory2 = new ReturnStatusHistory();
        assertThat(returnStatusHistory1).isNotEqualTo(returnStatusHistory2);

        returnStatusHistory2.setId(returnStatusHistory1.getId());
        assertThat(returnStatusHistory1).isEqualTo(returnStatusHistory2);

        returnStatusHistory2 = getReturnStatusHistorySample2();
        assertThat(returnStatusHistory1).isNotEqualTo(returnStatusHistory2);
    }

    @Test
    void returnedOrderTest() {
        ReturnStatusHistory returnStatusHistory = getReturnStatusHistoryRandomSampleGenerator();
        ReturnedOrder returnedOrderBack = getReturnedOrderRandomSampleGenerator();

        returnStatusHistory.setReturnedOrder(returnedOrderBack);
        assertThat(returnStatusHistory.getReturnedOrder()).isEqualTo(returnedOrderBack);

        returnStatusHistory.returnedOrder(null);
        assertThat(returnStatusHistory.getReturnedOrder()).isNull();
    }
}
