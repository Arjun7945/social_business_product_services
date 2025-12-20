import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';

const buttonActionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/button-action.component').then(m => m.ButtonActionComponent),
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default buttonActionRoute;
