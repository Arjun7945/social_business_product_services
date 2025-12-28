import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BotSession from './bot-session';
import BotSessionDetail from './bot-session-detail';
import BotSessionUpdate from './bot-session-update';
import BotSessionDeleteDialog from './bot-session-delete-dialog';

const BotSessionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<BotSession />} />
    <Route path="new" element={<BotSessionUpdate />} />
    <Route path=":id">
      <Route index element={<BotSessionDetail />} />
      <Route path="edit" element={<BotSessionUpdate />} />
      <Route path="delete" element={<BotSessionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BotSessionRoutes;
