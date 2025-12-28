import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CustomerOrder from './customer-order';
import CustomerOrderDetail from './customer-order-detail';
import CustomerOrderUpdate from './customer-order-update';
import CustomerOrderDeleteDialog from './customer-order-delete-dialog';

const CustomerOrderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CustomerOrder />} />
    <Route path="new" element={<CustomerOrderUpdate />} />
    <Route path=":id">
      <Route index element={<CustomerOrderDetail />} />
      <Route path="edit" element={<CustomerOrderUpdate />} />
      <Route path="delete" element={<CustomerOrderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CustomerOrderRoutes;
