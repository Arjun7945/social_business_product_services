
import React, { useState } from 'react';
import { DeviceFrameset } from 'react-device-frameset';
import '../assets/marvel-devices.min.css';
import '../assets/device-emulator.min.css';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const devices = [
    { name: 'iPhone X', value: 'iPhone X' },
    { name: 'iPhone 8', value: 'iPhone 8' },
    { name: 'iPhone 8 Plus', value: 'iPhone 8 Plus' },
    { name: 'iPhone 5s', value: 'iPhone 5s' },
    { name: 'iPad Mini', value: 'iPad Mini' },
    { name: 'MacBook Pro', value: 'MacBook Pro' },
];

const DevicePreviewPage = () => {
    const navigate = useNavigate();
    const [device, setDevice] = useState('iPhone X');
    const [color, setColor] = useState('gold');

    return (
        <div className="min-h-screen bg-gray-100 flex flex-col items-center py-10">
            {/* Controls */}
            <div className="bg-white p-4 rounded-xl shadow-md mb-8 flex items-center gap-4 z-10 sticky top-4">
                <button onClick={() => navigate('/')} className="p-2 hover:bg-gray-100 rounded-full text-gray-600">
                    <ArrowLeft className="w-5 h-5" />
                </button>

                <select
                    value={device}
                    onChange={(e) => setDevice(e.target.value)}
                    className="p-2 border rounded-md"
                >
                    {devices.map(d => <option key={d.value} value={d.value}>{d.name}</option>)}
                </select>

                <select
                    value={color}
                    onChange={(e) => setColor(e.target.value)}
                    className="p-2 border rounded-md"
                >
                    <option value="gold">Gold</option>
                    <option value="silver">Silver</option>
                    <option value="spacegray">Space Gray</option>
                    <option value="black">Black</option>
                </select>
            </div>

            {/* Device Frame */}
            <div className="transform scale-90 origin-top">
                <DeviceFrameset device={device} color={color}>
                    <iframe
                        src="/"
                        className="w-full h-full bg-white"
                        title="App Preview"
                        style={{ width: '100%', height: '100%' }}
                    />
                </DeviceFrameset>
            </div>
        </div>
    );
};

export default DevicePreviewPage;
