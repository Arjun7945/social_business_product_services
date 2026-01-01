/* eslint-disable */
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Package, Calendar, CreditCard, ChevronRight } from 'lucide-react';
import { getCustomerOrders } from '../api';
import dayjs from 'dayjs';

const OrderHistoryPage = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    // Retrieve mobile from localStorage (assuming it was saved during login)
    const mobile = localStorage.getItem('customerMobile');

    useEffect(() => {
        const fetchOrders = async () => {
            if (!mobile) {
                // Handle unauthenticated case or missing mobile
                setLoading(false);
                return;
            }
            try {
                const response = await getCustomerOrders(mobile);
                // Sort by ID desc (newest first)
                const sorted = response.data.sort((a: any, b: any) => b.id - a.id);
                setOrders(sorted);
            } catch (error) {
                console.error('Failed to fetch orders', error);
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();
    }, [mobile]);

    if (loading) {
        return (
            <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="min-vh-100 bg-light d-flex flex-column">
            {/* Header */}
            <div className="bg-white p-3 shadow-sm d-flex align-items-center sticky-top z-1">
                <button onClick={() => navigate('/ourCustomers/track-entry')} className="btn btn-link text-dark p-2 me-2 rounded-circle hover-bg-light">
                    <ArrowLeft size={24} />
                </button>
                <h1 className="h6 fw-bold mb-0 text-dark">My Orders</h1>
            </div>

            <div className="p-3">
                {orders.length === 0 ? (
                    <div className="text-center mt-5">
                        <div className="bg-white p-5 rounded-circle d-inline-flex mb-3 shadow-sm">
                            <Package size={48} className="text-muted" opacity={0.5} />
                        </div>
                        <h3 className="h6 text-muted">No orders found</h3>
                    </div>
                ) : (
                    <div className="d-flex flex-column gap-3">
                        {orders.map((order) => (
                            <div
                                key={order.id}
                                onClick={() => navigate(`/ourCustomers/track?orderId=${order.id}`)}
                                className="card border-0 shadow-sm rounded-4 overflow-hidden cursor-pointer active-scale-down"
                                style={{ transition: 'transform 0.1s' }}
                            >
                                <div className="card-body p-3">
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <span className="badge bg-primary bg-opacity-10 text-primary rounded-pill px-3 py-1 mb-2">
                                                #{order.id}
                                            </span>
                                            <h5 className="fw-bold text-dark mb-0">
                                                {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(order.totalAmount)}
                                            </h5>
                                        </div>
                                        <ChevronRight size={20} className="text-muted" />
                                    </div>

                                    <div className="d-flex flex-column gap-1 small text-muted">
                                        <div className="d-flex align-items-center gap-2">
                                            <Calendar size={14} />
                                            <span>{dayjs(order.orderTime).format('DD MMM YYYY, hh:mm A')}</span>
                                        </div>
                                        <div className="d-flex align-items-center gap-2">
                                            <CreditCard size={14} />
                                            <span className="text-uppercase">{order.paymentMethod}</span>
                                        </div>
                                        <div className="mt-2 text-primary fw-bold text-uppercase" style={{ fontSize: '0.75rem', letterSpacing: '0.5px' }}>
                                            {order.status.replace(/_/g, ' ')}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default OrderHistoryPage;
