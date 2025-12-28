import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Customer from './customer';
import DeliveryZone from './delivery-zone';
import DeliveryPerson from './delivery-person';
import FishProduct from './fish-product';
import ProductImage from './product-image';
import CustomerOrder from './customer-order';
import OrderItem from './order-item';
import ShoppingCart from './shopping-cart';
import CartItem from './cart-item';
import TeamMember from './team-member';
import BotSession from './bot-session';
import ButtonAction from './button-action';
import OrderStatusHistory from './order-status-history';
import ReturnedOrder from './returned-order';
import ReturnedOrderItem from './returned-order-item';
import ReturnStatusHistory from './return-status-history';
import RemovedOrderSummary from './removed-order-summary';
import RemovedUser from './removed-user';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="customer/*" element={<Customer />} />
        <Route path="delivery-zone/*" element={<DeliveryZone />} />
        <Route path="delivery-person/*" element={<DeliveryPerson />} />
        <Route path="fish-product/*" element={<FishProduct />} />
        <Route path="product-image/*" element={<ProductImage />} />
        <Route path="customer-order/*" element={<CustomerOrder />} />
        <Route path="order-item/*" element={<OrderItem />} />
        <Route path="shopping-cart/*" element={<ShoppingCart />} />
        <Route path="cart-item/*" element={<CartItem />} />
        <Route path="team-member/*" element={<TeamMember />} />
        <Route path="bot-session/*" element={<BotSession />} />
        <Route path="button-action/*" element={<ButtonAction />} />
        <Route path="order-status-history/*" element={<OrderStatusHistory />} />
        <Route path="returned-order/*" element={<ReturnedOrder />} />
        <Route path="returned-order-item/*" element={<ReturnedOrderItem />} />
        <Route path="return-status-history/*" element={<ReturnStatusHistory />} />
        <Route path="removed-order-summary/*" element={<RemovedOrderSummary />} />
        <Route path="removed-user/*" element={<RemovedUser />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
