
import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Phone, MapPin, Package, CheckCircle, Circle, Truck } from 'lucide-react';
import api from '../api';

const TrackOrderPage = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const orderId = searchParams.get('orderId') || 'ORD-12345'; // Default for demo

    const [orderData, setOrderData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchOrder = async () => {
            try {
                // Mock API Call - Replace with: await api.get(`/orders/${orderId}/track`);
                await new Promise(r => setTimeout(r, 1500));

                setOrderData({
                    id: orderId,
                    status: 'OUT_FOR_DELIVERY',
                    driver: {
                        name: 'Ramesh Kumar',
                        phone: '+919876543210',
                        vehicle: 'Honda Activa (KL-01-AB-1234)',
                        rating: 4.8
                    },
                    timeline: [
                        { status: 'ORDER_PLACED', label: 'Order Placed', time: '10:00 AM', completed: true },
                        { status: 'PREPARING', label: 'Preparing', time: '10:15 AM', completed: true },
                        { status: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', time: '10:45 AM', completed: true },
                        { status: 'DELIVERED', label: 'Delivered', time: 'Est. 11:00 AM', completed: false }
                    ]
                });
            } catch (err) {
                console.error("Failed to load order", err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchOrder();
    }, [orderId]);

    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            {/* Header */}
            <div className="bg-white p-4 shadow-sm flex items-center sticky top-0 z-10">
                <button onClick={() => navigate('/ourCustomer')} className="p-2 -ml-2 rounded-full hover:bg-gray-100">
                    <ArrowLeft className="w-6 h-6 text-gray-700" />
                </button>
                <div className="ml-4">
                    <h1 className="text-lg font-bold text-gray-900">Track Order</h1>
                    <p className="text-xs text-gray-500">ID: {orderData?.id}</p>
                </div>
            </div>

            <div className="flex-1 overflow-auto">
                {/* Map Placeholder */}
                <div className="h-64 bg-gray-200 relative w-full overflow-hidden">
                    {/* Abstract Map UI */}
                    <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#00CFA5_1px,transparent_1px)] [background-size:16px_16px]"></div>
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
                        <motion.div
                            animate={{ y: [0, -10, 0] }}
                            transition={{ repeat: Infinity, duration: 2 }}
                            className="bg-primary text-white p-3 rounded-full shadow-lg border-4 border-white"
                        >
                            <Truck className="w-6 h-6" />
                        </motion.div>
                        <div className="w-16 h-4 bg-black/10 blur-md rounded-[50%] absolute -bottom-2 left-0 animate-pulse"></div>
                    </div>
                    <div className="absolute bottom-4 right-4 bg-white px-3 py-1 rounded-md shadow text-xs font-semibold text-gray-600">
                        Live Tracking
                    </div>
                </div>

                {/* Driver Card */}
                <div className="bg-white p-5 -mt-6 rounded-t-3xl relative z-20 shadow-[0_-5px_20px_rgba(0,0,0,0.05)]">
                    <div className="w-12 h-1.5 bg-gray-200 rounded-full mx-auto mb-6" />

                    <div className="flex items-center justify-between mb-8">
                        <div className="flex items-center gap-4">
                            <div className="w-14 h-14 bg-gray-100 rounded-full flex items-center justify-center text-xl font-bold text-gray-400">
                                {orderData?.driver.name.charAt(0)}
                            </div>
                            <div>
                                <h3 className="font-bold text-gray-900 text-lg">{orderData?.driver.name}</h3>
                                <p className="text-sm text-gray-500">{orderData?.driver.vehicle}</p>
                                <div className="flex items-center gap-1 mt-1">
                                    <span className="text-yellow-400">★</span>
                                    <span className="text-xs font-bold text-gray-700">{orderData?.driver.rating}</span>
                                </div>
                            </div>
                        </div>
                        <a
                            href={`tel:${orderData?.driver.phone}`} // Native phone call
                            className="bg-green-100 p-3 rounded-full text-green-600 hover:bg-green-200 transition-colors"
                        >
                            <Phone className="w-6 h-6" />
                        </a>
                    </div>

                    {/* Vertical Timeline */}
                    <div className="space-y-0 relative pl-2">
                        {/* Timeline Line */}
                        <div className="absolute left-[19px] top-2 bottom-8 w-0.5 bg-gray-100"></div>

                        {orderData?.timeline.map((item, index) => (
                            <div key={index} className="flex gap-6 relative pb-8 last:pb-0 group">
                                <div className="relative z-10">
                                    {item.completed ? (
                                        <div className="w-10 h-10 bg-primary/10 rounded-full flex items-center justify-center border-2 border-white shadow-sm">
                                            <CheckCircle className="w-5 h-5 text-primary" />
                                        </div>
                                    ) : (
                                        <div className="w-10 h-10 bg-gray-50 rounded-full flex items-center justify-center border-2 border-white shadow-sm">
                                            <Circle className="w-5 h-5 text-gray-300" />
                                        </div>
                                    )}
                                </div>
                                <div className={`pt-2 ${item.completed ? 'opacity-100' : 'opacity-40'}`}>
                                    <h4 className="font-bold text-gray-900 leading-none mb-1">{item.label}</h4>
                                    <p className="text-xs text-gray-500">{item.time}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TrackOrderPage;
