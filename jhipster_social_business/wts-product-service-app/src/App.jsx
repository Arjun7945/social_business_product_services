import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import OtpPage from './pages/OtpPage';
import SuccessFailPage from './pages/SuccessFailPage';
import GlobalBackground from './components/GlobalBackground';

import TrackOrderPage from './pages/TrackOrderPage';
import DevicePreviewPage from './pages/DevicePreviewPage';
import ReturnOrderPage from './pages/ReturnOrderPage';
import CustomerLayout from './components/layout/CustomerLayout';
import MainLayout from './components/layout/MainLayout';

function App() {
  return (
    <Router>
      <GlobalBackground />

      <Routes>
        {/* Phase 0: Customer Routes moved to /ourCustomer */}
        <Route path="/ourCustomer" element={<CustomerLayout />}>
          <Route index element={<LandingPage />} />
          <Route path="login" element={<LoginPage />} />
          <Route path="otp" element={<OtpPage />} />
          <Route path="status" element={<SuccessFailPage />} />
          <Route path="track" element={<TrackOrderPage />} />
          <Route path="return" element={<ReturnOrderPage />} />
        </Route>

        {/* Phase 0: Root route is now the Internal Admin App */}
        <Route path="/" element={<MainLayout />}>
          {/* Add admin routes here later */}
        </Route>

        {/* Dev Tools - Keep at root or move as needed */}
        <Route path="/preview" element={<DevicePreviewPage />} />

        {/* Fallback to Internal App for unknown routes for now, or 404 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
