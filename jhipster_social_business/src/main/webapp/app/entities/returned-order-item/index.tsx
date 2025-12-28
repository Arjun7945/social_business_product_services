import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ReturnedOrderItem from './returned-order-item';
import ReturnedOrderItemDetail from './returned-order-item-detail';
import ReturnedOrderItemUpdate from './returned-order-item-update';
import ReturnedOrderItemDeleteDialog from './returned-order-item-delete-dialog';

const ReturnedOrderItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ReturnedOrderItem />} />
    <Route path="new" element={<ReturnedOrderItemUpdate />} />
    <Route path=":id">
      <Route index element={<ReturnedOrderItemDetail />} />
      <Route path="edit" element={<ReturnedOrderItemUpdate />} />
      <Route path="delete" element={<ReturnedOrderItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReturnedOrderItemRoutes;
