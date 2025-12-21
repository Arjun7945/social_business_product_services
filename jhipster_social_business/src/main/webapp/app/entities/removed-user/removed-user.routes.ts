import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RemovedUserResolve from './route/removed-user-routing-resolve.service';

const removedUserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/removed-user.component').then(m => m.RemovedUserComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/removed-user-detail.component').then(m => m.RemovedUserDetailComponent),
    resolve: {
      removedUser: RemovedUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default removedUserRoute;
