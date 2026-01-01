import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Phone, Truck, CheckCircle, Circle } from 'lucide-react';
// import api from '../api';

const TrackOrderPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('orderId') || 'ORD-12345'; // Default for demo

  const [orderData, setOrderData] = useState<any>(null);
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
            rating: 4.8,
          },
          timeline: [
            { status: 'ORDER_PLACED', label: 'Order Placed', time: '10:00 AM', completed: true },
            { status: 'PREPARING', label: 'Preparing', time: '10:15 AM', completed: true },
            { status: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', time: '10:45 AM', completed: true },
            { status: 'DELIVERED', label: 'Delivered', time: 'Est. 11:00 AM', completed: false },
          ],
        });
      } catch (err) {
        console.error('Failed to load order', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);

  if (isLoading) {
    return (
      <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-vh-100 bg-light d-flex flex-column">
      {/* Header */}
      <div className="bg-white p-3 shadow-sm d-flex align-items-center sticky-top z-1">
        <button onClick={() => navigate('/ourCustomers')} className="btn btn-link text-dark p-2 me-2 rounded-circle hover-bg-light">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="h6 fw-bold mb-0 text-dark">Track Order</h1>
          <p className="small text-muted mb-0">ID: {orderData?.id}</p>
        </div>
      </div>

      <div className="flex-grow-1 overflow-auto">
        {/* Map Placeholder */}
        <div className="bg-secondary position-relative w-100 overflow-hidden" style={{ height: '16rem' }}>
          {/* Abstract Map UI */}
          <div
            className="position-absolute top-0 start-0 w-100 h-100 opacity-10"
            style={{
              background: 'radial-gradient(#00CFA5 1px, transparent 1px)',
              backgroundSize: '16px 16px',
            }}
          ></div>
          <div className="position-absolute top-50 start-50 translate-middle">
            <motion.div
              animate={{ y: [0, -10, 0] }}
              transition={{ repeat: Infinity, duration: 2 }}
              className="bg-primary text-white p-2 rounded-circle shadow border border-4 border-white"
            >
              <Truck size={24} />
            </motion.div>
            <div
              className="position-absolute bottom-0 start-0 w-100 h-25 bg-black opacity-10 blur rounded-pill animate-pulse"
              style={{ width: '4rem', filter: 'blur(4px)' }}
            ></div>
          </div>
          <div className="position-absolute bottom-0 end-0 m-3 px-2 py-1 bg-white rounded shadow-sm text-secondary small fw-bold">
            Live Tracking
          </div>
        </div>

        {/* Driver Card */}
        <div
          className="bg-white p-4 rounded-top-4 position-relative shadow-custom"
          style={{
            marginTop: '-1.5rem',
            zIndex: 20,
            boxShadow: '0 -5px 20px rgba(0,0,0,0.05)',
            borderTopLeftRadius: '1.5rem',
            borderTopRightRadius: '1.5rem',
          }}
        >
          <div className="bg-light rounded-pill mx-auto mb-4" style={{ width: '3rem', height: '0.375rem' }} />

          <div className="d-flex align-items-center justify-content-between mb-4">
            <div className="d-flex align-items-center gap-3">
              <div
                className="bg-light rounded-circle d-flex align-items-center justify-content-center text-secondary fw-bold"
                style={{ width: '3.5rem', height: '3.5rem', fontSize: '1.25rem' }}
              >
                {orderData?.driver.name.charAt(0)}
              </div>
              <div>
                <h3 className="h6 fw-bold text-dark mb-0">{orderData?.driver.name}</h3>
                <p className="small text-muted mb-1">{orderData?.driver.vehicle}</p>
                <div className="d-flex align-items-center gap-1">
                  <span className="text-warning">★</span>
                  <span className="small fw-bold text-dark">{orderData?.driver.rating}</span>
                </div>
              </div>
            </div>
            <a
              href={`tel:${orderData?.driver.phone}`}
              className="btn btn-success rounded-circle p-3 d-flex align-items-center justify-content-center bg-opacity-10 text-success border-0"
              style={{ width: '3.5rem', height: '3.5rem' }}
            >
              <Phone size={24} />
            </a>
          </div>

          {/* Vertical Timeline */}
          <div className="position-relative ps-2">
            {/* Timeline Line */}
            <div className="position-absolute bg-light" style={{ left: '19px', top: '0.5rem', bottom: '2rem', width: '2px' }}></div>

            {orderData?.timeline.map((item: any, index: number) => (
              <div key={index} className="d-flex gap-4 position-relative pb-4">
                <div className="position-relative" style={{ zIndex: 10 }}>
                  {item.completed ? (
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center bg-primary bg-opacity-10 border border-2 border-white shadow-sm"
                      style={{ width: '2.5rem', height: '2.5rem' }}
                    >
                      <CheckCircle size={20} className="text-primary" />
                    </div>
                  ) : (
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center bg-light border border-2 border-white shadow-sm"
                      style={{ width: '2.5rem', height: '2.5rem' }}
                    >
                      <Circle size={20} className="text-secondary opacity-50" />
                    </div>
                  )}
                </div>
                <div className={`pt-1 ${item.completed ? 'opacity-100' : 'opacity-50'}`}>
                  <h4 className="h6 fw-bold text-dark mb-0">{item.label}</h4>
                  <p className="small text-muted">{item.time}</p>
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
