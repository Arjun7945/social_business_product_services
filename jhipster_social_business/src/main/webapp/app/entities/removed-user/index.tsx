import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RemovedUser from './removed-user';
import RemovedUserDetail from './removed-user-detail';
import RemovedUserUpdate from './removed-user-update';
import RemovedUserDeleteDialog from './removed-user-delete-dialog';
import RemovedUserRestoreDialog from './removed-user-restore-dialog';

const RemovedUserRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RemovedUser />} />
    <Route path="new" element={<RemovedUserUpdate />} />
    <Route path=":id">
      <Route index element={<RemovedUserDetail />} />
      <Route path="edit" element={<RemovedUserUpdate />} />
      <Route path="delete" element={<RemovedUserDeleteDialog />} />
      <Route path="restore" element={<RemovedUserRestoreDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RemovedUserRoutes;
