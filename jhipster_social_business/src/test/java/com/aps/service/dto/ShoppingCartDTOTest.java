package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoppingCartDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoppingCartDTO.class);
        ShoppingCartDTO shoppingCartDTO1 = new ShoppingCartDTO();
        shoppingCartDTO1.setId(1L);
        ShoppingCartDTO shoppingCartDTO2 = new ShoppingCartDTO();
        assertThat(shoppingCartDTO1).isNotEqualTo(shoppingCartDTO2);
        shoppingCartDTO2.setId(shoppingCartDTO1.getId());
        assertThat(shoppingCartDTO1).isEqualTo(shoppingCartDTO2);
        shoppingCartDTO2.setId(2L);
        assertThat(shoppingCartDTO1).isNotEqualTo(shoppingCartDTO2);
        shoppingCartDTO1.setId(null);
        assertThat(shoppingCartDTO1).isNotEqualTo(shoppingCartDTO2);
    }
}
