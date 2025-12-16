package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FishProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FishProductDTO.class);
        FishProductDTO fishProductDTO1 = new FishProductDTO();
        fishProductDTO1.setId(1L);
        FishProductDTO fishProductDTO2 = new FishProductDTO();
        assertThat(fishProductDTO1).isNotEqualTo(fishProductDTO2);
        fishProductDTO2.setId(fishProductDTO1.getId());
        assertThat(fishProductDTO1).isEqualTo(fishProductDTO2);
        fishProductDTO2.setId(2L);
        assertThat(fishProductDTO1).isNotEqualTo(fishProductDTO2);
        fishProductDTO1.setId(null);
        assertThat(fishProductDTO1).isNotEqualTo(fishProductDTO2);
    }
}
