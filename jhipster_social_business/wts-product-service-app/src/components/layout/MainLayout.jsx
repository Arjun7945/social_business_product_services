import React from 'react';
import { Outlet } from 'react-router-dom';

const MainLayout = () => {
    return (
        <div className="internal-app">
            {/* Internal app specific header/sidebar/footer will go here */}
            <div className="p-10 flex flex-col items-center justify-center min-h-screen">
                <h1 className="text-3xl font-bold mb-4">Internal Admin Application</h1>
                <p className="text-gray-600">Migration in progress...</p>
            </div>
            <Outlet />
        </div>
    );
};

export default MainLayout;
