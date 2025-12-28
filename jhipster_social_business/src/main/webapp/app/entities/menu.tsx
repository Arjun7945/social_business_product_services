import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/customer">
        <Translate contentKey="global.menu.entities.customer" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/delivery-zone">
        <Translate contentKey="global.menu.entities.deliveryZone" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/delivery-person">
        <Translate contentKey="global.menu.entities.deliveryPerson" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/fish-product">
        <Translate contentKey="global.menu.entities.fishProduct" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product-image">
        <Translate contentKey="global.menu.entities.productImage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/customer-order">
        <Translate contentKey="global.menu.entities.customerOrder" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-item">
        <Translate contentKey="global.menu.entities.orderItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/shopping-cart">
        <Translate contentKey="global.menu.entities.shoppingCart" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cart-item">
        <Translate contentKey="global.menu.entities.cartItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/team-member">
        <Translate contentKey="global.menu.entities.teamMember" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/bot-session">
        <Translate contentKey="global.menu.entities.botSession" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/button-action">
        <Translate contentKey="global.menu.entities.buttonAction" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-status-history">
        <Translate contentKey="global.menu.entities.orderStatusHistory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/returned-order">
        <Translate contentKey="global.menu.entities.returnedOrder" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/returned-order-item">
        <Translate contentKey="global.menu.entities.returnedOrderItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/return-status-history">
        <Translate contentKey="global.menu.entities.returnStatusHistory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/removed-order-summary">
        <Translate contentKey="global.menu.entities.removedOrderSummary" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/removed-user">
        <Translate contentKey="global.menu.entities.removedUser" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
