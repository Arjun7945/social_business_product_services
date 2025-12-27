import React from 'react';
import { Outlet } from 'react-router-dom';

const CustomerLayout = () => {
  return (
    <div className="customer-app">
      {/* Customer specific header/navigation can go here later */}
      <Outlet />
      {/* Customer specific footer can go here later */}
    </div>
  );
};

export default CustomerLayout;
