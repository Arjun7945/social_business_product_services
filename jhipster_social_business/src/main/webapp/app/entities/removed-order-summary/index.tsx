import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RemovedOrderSummary from './removed-order-summary';
import RemovedOrderSummaryDetail from './removed-order-summary-detail';
import RemovedOrderSummaryUpdate from './removed-order-summary-update';
import RemovedOrderSummaryDeleteDialog from './removed-order-summary-delete-dialog';

const RemovedOrderSummaryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RemovedOrderSummary />} />
    <Route path="new" element={<RemovedOrderSummaryUpdate />} />
    <Route path=":id">
      <Route index element={<RemovedOrderSummaryDetail />} />
      <Route path="edit" element={<RemovedOrderSummaryUpdate />} />
      <Route path="delete" element={<RemovedOrderSummaryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RemovedOrderSummaryRoutes;
