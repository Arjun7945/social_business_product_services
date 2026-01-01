import React from 'react';
import { Route, Routes } from 'react-router-dom';
import CustomerLayout from './components/CustomerLayout';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import OtpPage from './pages/OtpPage';
import SuccessFailPage from './pages/SuccessFailPage';
import TrackOrderPage from './pages/TrackOrderPage';
import ReturnOrderPage from './pages/ReturnOrderPage';

const CustomerRoutes = () => {
  return (
    <Routes>
      <Route element={<CustomerLayout />}>
        <Route index element={<LandingPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="otp" element={<OtpPage />} />
        <Route path="status" element={<SuccessFailPage />} />
        <Route path="track" element={<TrackOrderPage />} />
        <Route path="return" element={<ReturnOrderPage />} />
      </Route>
    </Routes>
  );
};

export default CustomerRoutes;
