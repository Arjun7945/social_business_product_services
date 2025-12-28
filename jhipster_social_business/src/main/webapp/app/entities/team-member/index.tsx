import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TeamMember from './team-member';
import TeamMemberDetail from './team-member-detail';
import TeamMemberUpdate from './team-member-update';
import TeamMemberDeleteDialog from './team-member-delete-dialog';

const TeamMemberRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TeamMember />} />
    <Route path="new" element={<TeamMemberUpdate />} />
    <Route path=":id">
      <Route index element={<TeamMemberDetail />} />
      <Route path="edit" element={<TeamMemberUpdate />} />
      <Route path="delete" element={<TeamMemberDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TeamMemberRoutes;
