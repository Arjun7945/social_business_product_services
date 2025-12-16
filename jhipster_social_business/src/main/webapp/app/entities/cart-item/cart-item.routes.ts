import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CartItemResolve from './route/cart-item-routing-resolve.service';

const cartItemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cart-item.component').then(m => m.CartItemComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cart-item-detail.component').then(m => m.CartItemDetailComponent),
    resolve: {
      cartItem: CartItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cart-item-update.component').then(m => m.CartItemUpdateComponent),
    resolve: {
      cartItem: CartItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cart-item-update.component').then(m => m.CartItemUpdateComponent),
    resolve: {
      cartItem: CartItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cartItemRoute;
