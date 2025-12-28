import customer from 'app/entities/customer/customer.reducer';
import deliveryZone from 'app/entities/delivery-zone/delivery-zone.reducer';
import deliveryPerson from 'app/entities/delivery-person/delivery-person.reducer';
import fishProduct from 'app/entities/fish-product/fish-product.reducer';
import productImage from 'app/entities/product-image/product-image.reducer';
import customerOrder from 'app/entities/customer-order/customer-order.reducer';
import orderItem from 'app/entities/order-item/order-item.reducer';
import shoppingCart from 'app/entities/shopping-cart/shopping-cart.reducer';
import cartItem from 'app/entities/cart-item/cart-item.reducer';
import teamMember from 'app/entities/team-member/team-member.reducer';
import botSession from 'app/entities/bot-session/bot-session.reducer';
import buttonAction from 'app/entities/button-action/button-action.reducer';
import orderStatusHistory from 'app/entities/order-status-history/order-status-history.reducer';
import returnedOrder from 'app/entities/returned-order/returned-order.reducer';
import returnedOrderItem from 'app/entities/returned-order-item/returned-order-item.reducer';
import returnStatusHistory from 'app/entities/return-status-history/return-status-history.reducer';
import removedOrderSummary from 'app/entities/removed-order-summary/removed-order-summary.reducer';
import removedUser from 'app/entities/removed-user/removed-user.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  customer,
  deliveryZone,
  deliveryPerson,
  fishProduct,
  productImage,
  customerOrder,
  orderItem,
  shoppingCart,
  cartItem,
  teamMember,
  botSession,
  buttonAction,
  orderStatusHistory,
  returnedOrder,
  returnedOrderItem,
  returnStatusHistory,
  removedOrderSummary,
  removedUser,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
