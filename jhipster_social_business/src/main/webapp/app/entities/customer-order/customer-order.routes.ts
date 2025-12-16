import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CustomerOrderResolve from './route/customer-order-routing-resolve.service';

const customerOrderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/customer-order.component').then(m => m.CustomerOrderComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/customer-order-detail.component').then(m => m.CustomerOrderDetailComponent),
    resolve: {
      customerOrder: CustomerOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/customer-order-update.component').then(m => m.CustomerOrderUpdateComponent),
    resolve: {
      customerOrder: CustomerOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/customer-order-update.component').then(m => m.CustomerOrderUpdateComponent),
    resolve: {
      customerOrder: CustomerOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default customerOrderRoute;
