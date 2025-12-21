import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RemovedOrderSummaryResolve from './route/removed-order-summary-routing-resolve.service';

const removedOrderSummaryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/removed-order-summary.component').then(m => m.RemovedOrderSummaryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/removed-order-summary-detail.component').then(m => m.RemovedOrderSummaryDetailComponent),
    resolve: {
      removedOrderSummary: RemovedOrderSummaryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default removedOrderSummaryRoute;
