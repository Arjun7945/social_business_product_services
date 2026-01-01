/* eslint-disable */
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, List } from 'lucide-react';
import GlobalBackground from '../components/GlobalBackground';

const OrderEntryPage = () => {
    const navigate = useNavigate();
    const [orderId, setOrderId] = useState('');

    const handleTrack = (e: React.FormEvent) => {
        e.preventDefault();
        if (orderId.trim()) {
            navigate(`/ourCustomers/track?orderId=${orderId.trim()}`);
        }
    };

    return (
        <div className="min-vh-100 d-flex flex-column position-relative overflow-hidden">
            <GlobalBackground />

            <div className="flex-grow-1 d-flex flex-column justify-content-center p-4" style={{ zIndex: 10 }}>
                <div className="text-center mb-5">
                    <h1 className="display-5 fw-bold text-dark mb-2">Track Your Order</h1>
                    <p className="text-muted">Enter your Order ID to see real-time status</p>
                </div>

                <div className="card border-0 shadow-lg rounded-4 p-4 mb-4 bg-white bg-opacity-75 backdrop-blur">
                    <form onSubmit={handleTrack}>
                        <div className="mb-4">
                            <label htmlFor="orderId" className="form-label fw-bold text-dark small text-uppercase">Order ID</label>
                            <div className="input-group input-group-lg">
                                <span className="input-group-text bg-white border-end-0 text-muted ps-3">
                                    <Search size={20} />
                                </span>
                                <input
                                    type="text"
                                    id="orderId"
                                    className="form-control border-start-0 ps-0 text-dark fw-bold"
                                    placeholder="e.g. 12345"
                                    value={orderId}
                                    onChange={(e) => setOrderId(e.target.value)}
                                    style={{ boxShadow: 'none' }}
                                />
                            </div>
                        </div>
                        <button
                            type="submit"
                            className="btn btn-primary btn-lg w-100 rounded-pill fw-bold shadow-sm"
                            disabled={!orderId.trim()}
                        >
                            Track Order
                        </button>
                    </form>
                </div>

                <div className="text-center">
                    <p className="text-muted small mb-3">Or view all your past orders</p>
                    <button
                        onClick={() => navigate('/ourCustomers/history')}
                        className="btn btn-light btn-lg w-100 rounded-pill fw-bold text-primary d-flex align-items-center justify-content-center gap-2 shadow-sm"
                    >
                        <List size={20} />
                        Show All Orders
                    </button>
                </div>
            </div>
        </div>
    );
};

export default OrderEntryPage;
