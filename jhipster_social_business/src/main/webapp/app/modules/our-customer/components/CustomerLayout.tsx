import React from 'react';
import { Outlet } from 'react-router-dom';
import GlobalBackground from '../components/GlobalBackground';

const CustomerLayout = () => {
  React.useEffect(() => {
    document.body.classList.add('customer-flow-active');
    return () => {
      document.body.classList.remove('customer-flow-active');
    };
  }, []);

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
