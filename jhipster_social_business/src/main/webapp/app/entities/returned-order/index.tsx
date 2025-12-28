import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ReturnedOrder from './returned-order';
import ReturnedOrderDetail from './returned-order-detail';
import ReturnedOrderUpdate from './returned-order-update';
import ReturnedOrderDeleteDialog from './returned-order-delete-dialog';

const ReturnedOrderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ReturnedOrder />} />
    <Route path="new" element={<ReturnedOrderUpdate />} />
    <Route path=":id">
      <Route index element={<ReturnedOrderDetail />} />
      <Route path="edit" element={<ReturnedOrderUpdate />} />
      <Route path="delete" element={<ReturnedOrderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReturnedOrderRoutes;
