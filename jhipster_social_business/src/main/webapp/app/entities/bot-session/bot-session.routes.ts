import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BotSessionResolve from './route/bot-session-routing-resolve.service';

const botSessionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/bot-session.component').then(m => m.BotSessionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/bot-session-detail.component').then(m => m.BotSessionDetailComponent),
    resolve: {
      botSession: BotSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/bot-session-update.component').then(m => m.BotSessionUpdateComponent),
    resolve: {
      botSession: BotSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/bot-session-update.component').then(m => m.BotSessionUpdateComponent),
    resolve: {
      botSession: BotSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default botSessionRoute;
