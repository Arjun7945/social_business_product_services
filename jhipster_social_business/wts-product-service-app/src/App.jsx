
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import OtpPage from './pages/OtpPage';
import SuccessFailPage from './pages/SuccessFailPage';

import TrackOrderPage from './pages/TrackOrderPage';
import DevicePreviewPage from './pages/DevicePreviewPage';

const ReturnOrderPage = () => <div className="p-10 text-center"><h1>Return Order (Phase 2)</h1></div>;

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/otp" element={<OtpPage />} />
        <Route path="/status" element={<SuccessFailPage />} />

        {/* Phase 2 Routes */}
        <Route path="/track" element={<TrackOrderPage />} />
        <Route path="/return" element={<ReturnOrderPage />} />

        {/* Dev Tools */}
        <Route path="/preview" element={<DevicePreviewPage />} />
      </Routes>
    </Router>
  );
}

export default App;
