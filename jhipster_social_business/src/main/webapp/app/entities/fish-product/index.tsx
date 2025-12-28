import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FishProduct from './fish-product';
import FishProductDetail from './fish-product-detail';
import FishProductUpdate from './fish-product-update';
import FishProductDeleteDialog from './fish-product-delete-dialog';

const FishProductRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FishProduct />} />
    <Route path="new" element={<FishProductUpdate />} />
    <Route path=":id">
      <Route index element={<FishProductDetail />} />
      <Route path="edit" element={<FishProductUpdate />} />
      <Route path="delete" element={<FishProductDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FishProductRoutes;
