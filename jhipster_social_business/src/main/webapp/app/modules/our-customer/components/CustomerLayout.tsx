import React from 'react';
import { Outlet } from 'react-router-dom';
import GlobalBackground from '../components/GlobalBackground';

const CustomerLayout = () => {
  return (
    <div className="customer-app">
      <GlobalBackground />
      {/* Customer specific header/navigation can go here later */}
      <Outlet />
      {/* Customer specific footer can go here later */}
    </div>
  );
};

export default CustomerLayout;
