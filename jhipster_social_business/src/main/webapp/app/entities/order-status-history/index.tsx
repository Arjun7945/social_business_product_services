import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OrderStatusHistory from './order-status-history';
import OrderStatusHistoryDetail from './order-status-history-detail';
import OrderStatusHistoryUpdate from './order-status-history-update';
import OrderStatusHistoryDeleteDialog from './order-status-history-delete-dialog';

const OrderStatusHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OrderStatusHistory />} />
    <Route path="new" element={<OrderStatusHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<OrderStatusHistoryDetail />} />
      <Route path="edit" element={<OrderStatusHistoryUpdate />} />
      <Route path="delete" element={<OrderStatusHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OrderStatusHistoryRoutes;
