import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DeliveryPerson from './delivery-person';
import DeliveryPersonDetail from './delivery-person-detail';
import DeliveryPersonUpdate from './delivery-person-update';
import DeliveryPersonDeleteDialog from './delivery-person-delete-dialog';

const DeliveryPersonRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DeliveryPerson />} />
    <Route path="new" element={<DeliveryPersonUpdate />} />
    <Route path=":id">
      <Route index element={<DeliveryPersonDetail />} />
      <Route path="edit" element={<DeliveryPersonUpdate />} />
      <Route path="delete" element={<DeliveryPersonDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DeliveryPersonRoutes;
