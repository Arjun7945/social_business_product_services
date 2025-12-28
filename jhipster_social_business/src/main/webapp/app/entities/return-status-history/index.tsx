import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ReturnStatusHistory from './return-status-history';
import ReturnStatusHistoryDetail from './return-status-history-detail';
import ReturnStatusHistoryUpdate from './return-status-history-update';
import ReturnStatusHistoryDeleteDialog from './return-status-history-delete-dialog';

const ReturnStatusHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ReturnStatusHistory />} />
    <Route path="new" element={<ReturnStatusHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<ReturnStatusHistoryDetail />} />
      <Route path="edit" element={<ReturnStatusHistoryUpdate />} />
      <Route path="delete" element={<ReturnStatusHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReturnStatusHistoryRoutes;
