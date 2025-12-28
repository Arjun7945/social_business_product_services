import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DeliveryZone from './delivery-zone';
import DeliveryZoneDetail from './delivery-zone-detail';
import DeliveryZoneUpdate from './delivery-zone-update';
import DeliveryZoneDeleteDialog from './delivery-zone-delete-dialog';

const DeliveryZoneRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DeliveryZone />} />
    <Route path="new" element={<DeliveryZoneUpdate />} />
    <Route path=":id">
      <Route index element={<DeliveryZoneDetail />} />
      <Route path="edit" element={<DeliveryZoneUpdate />} />
      <Route path="delete" element={<DeliveryZoneDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DeliveryZoneRoutes;
