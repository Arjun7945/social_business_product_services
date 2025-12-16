import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ProductImageResolve from './route/product-image-routing-resolve.service';

const productImageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/product-image.component').then(m => m.ProductImageComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/product-image-detail.component').then(m => m.ProductImageDetailComponent),
    resolve: {
      productImage: ProductImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/product-image-update.component').then(m => m.ProductImageUpdateComponent),
    resolve: {
      productImage: ProductImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/product-image-update.component').then(m => m.ProductImageUpdateComponent),
    resolve: {
      productImage: ProductImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default productImageRoute;
