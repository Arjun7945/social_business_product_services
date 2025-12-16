import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FishProductResolve from './route/fish-product-routing-resolve.service';

const fishProductRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/fish-product.component').then(m => m.FishProductComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/fish-product-detail.component').then(m => m.FishProductDetailComponent),
    resolve: {
      fishProduct: FishProductResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/fish-product-update.component').then(m => m.FishProductUpdateComponent),
    resolve: {
      fishProduct: FishProductResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/fish-product-update.component').then(m => m.FishProductUpdateComponent),
    resolve: {
      fishProduct: FishProductResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default fishProductRoute;
