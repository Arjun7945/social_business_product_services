import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ShoppingCart from './shopping-cart';
import ShoppingCartDetail from './shopping-cart-detail';
import ShoppingCartUpdate from './shopping-cart-update';
import ShoppingCartDeleteDialog from './shopping-cart-delete-dialog';

const ShoppingCartRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ShoppingCart />} />
    <Route path="new" element={<ShoppingCartUpdate />} />
    <Route path=":id">
      <Route index element={<ShoppingCartDetail />} />
      <Route path="edit" element={<ShoppingCartUpdate />} />
      <Route path="delete" element={<ShoppingCartDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ShoppingCartRoutes;
