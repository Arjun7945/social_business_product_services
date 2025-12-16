import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ShoppingCartResolve from './route/shopping-cart-routing-resolve.service';

const shoppingCartRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/shopping-cart.component').then(m => m.ShoppingCartComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/shopping-cart-detail.component').then(m => m.ShoppingCartDetailComponent),
    resolve: {
      shoppingCart: ShoppingCartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/shopping-cart-update.component').then(m => m.ShoppingCartUpdateComponent),
    resolve: {
      shoppingCart: ShoppingCartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/shopping-cart-update.component').then(m => m.ShoppingCartUpdateComponent),
    resolve: {
      shoppingCart: ShoppingCartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default shoppingCartRoute;
