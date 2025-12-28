import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ButtonAction from './button-action';
import ButtonActionDetail from './button-action-detail';
import ButtonActionUpdate from './button-action-update';
import ButtonActionDeleteDialog from './button-action-delete-dialog';

const ButtonActionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ButtonAction />} />
    <Route path="new" element={<ButtonActionUpdate />} />
    <Route path=":id">
      <Route index element={<ButtonActionDetail />} />
      <Route path="edit" element={<ButtonActionUpdate />} />
      <Route path="delete" element={<ButtonActionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ButtonActionRoutes;
