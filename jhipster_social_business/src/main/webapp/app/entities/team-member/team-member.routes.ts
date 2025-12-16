import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TeamMemberResolve from './route/team-member-routing-resolve.service';

const teamMemberRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/team-member.component').then(m => m.TeamMemberComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/team-member-detail.component').then(m => m.TeamMemberDetailComponent),
    resolve: {
      teamMember: TeamMemberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/team-member-update.component').then(m => m.TeamMemberUpdateComponent),
    resolve: {
      teamMember: TeamMemberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/team-member-update.component').then(m => m.TeamMemberUpdateComponent),
    resolve: {
      teamMember: TeamMemberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teamMemberRoute;
