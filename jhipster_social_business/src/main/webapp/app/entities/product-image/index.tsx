import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductImage from './product-image';
import ProductImageDetail from './product-image-detail';
import ProductImageUpdate from './product-image-update';
import ProductImageDeleteDialog from './product-image-delete-dialog';

const ProductImageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductImage />} />
    <Route path="new" element={<ProductImageUpdate />} />
    <Route path=":id">
      <Route index element={<ProductImageDetail />} />
      <Route path="edit" element={<ProductImageUpdate />} />
      <Route path="delete" element={<ProductImageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductImageRoutes;
