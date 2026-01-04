/* eslint-disable */
import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Phone, Truck, CheckCircle, Circle, FileText, XCircle } from 'lucide-react';
import { getOrderDetails } from '../api';
import dayjs from 'dayjs';
import Lottie from 'lottie-react';
import noOrderAnimation from 'app/assets/animations/No_order_id_Found.json';

const TrackOrderPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('orderId');

  const [orderData, setOrderData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!orderId) {
      setError('No Order ID provided');
      setLoading(false);
      return;
    }

    const fetchOrder = async () => {
      try {
        const response = await getOrderDetails(orderId);
        setOrderData(response.data);
      } catch (err) {
        console.error('Failed to load order', err);
        setError('Order not found or access denied');
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);

  // Logic to determine Estimated Time / Total Time
  const getEstimatedTime = (order: any) => {
    if (!order) return '-- mins';

    const start = dayjs(order.orderTime);
    let end = dayjs(); // Default to now

    // If order delivered, use delivered time (confirmedAt)
    if (order.status === 'DELIVERED' || order.status === 'ORDER_DELIVERED_SUCESSFULLY') {
      if (order.confirmedAt) {
        end = dayjs(order.confirmedAt);
      }
    }

    const diffMins = end.diff(start, 'minute');
    return `${diffMins} mins`;
  };

  // Status mapping logic
  const getTimeline = (order: any) => {
    if (!order) return [];

    const steps = [
      { id: 'ORDER_PLACED', label: 'Order Placed', mapped: ['ORDER_NOT_TAKEN'] },
      { id: 'ORDER_ONWAY', label: 'Order Onway', mapped: ['DELIVERY_ONWAY'] },
      { id: 'PAYMENT_PENDING', label: 'Payment Pending', mapped: [] }, // Virtual Step based on data
      { id: 'PAYMENT_STATUS', label: 'Payment Status', mapped: [] }, // Virtual Step
      { id: 'DELIVERED', label: 'Order Delivered Successfully', mapped: ['ORDER_DELIVERED_SUCESSFULLY'] }
    ];

    // Determine current step index based on order status
    let currentIndex = -1;
    const currentStatus = order.status;

    // Find custom logic for mapping
    steps.forEach((step, index) => {
      if (step.mapped.includes(currentStatus)) {
        currentIndex = index;
      }
    });

    // Fallback if status not matched exactly in list
    if (currentIndex === -1) {
      if (currentStatus === 'CANCELLED' || currentStatus === 'ORDER_FAILED') currentIndex = -2;
    }

    return steps.map((step, index) => {
      let isCompleted = index <= currentIndex;
      const isCurrent = index === currentIndex;

      // LOGIC REFINEMENTS

      // 1. Order Placed is ALWAYS completed if we have an order
      if (step.id === 'ORDER_PLACED') {
        isCompleted = true;
      }

      // 2. Order Onway: Completed if status mapped OR if we have moved to payment (method selected)
      if (step.id === 'ORDER_ONWAY') {
        if (order.paymentMethod && order.paymentMethod !== 'NOT_SELECTED') {
          isCompleted = true;
        }
      }

      // 3. Payment Pending: Completed if payment method selected
      if (step.id === 'PAYMENT_PENDING') {
        if (order.paymentMethod && order.paymentMethod !== 'NOT_SELECTED') {
          isCompleted = true;
        }
      }

      // 4. Handle ORDER_FAILED logic
      let isFailed = false;
      let label = step.label;

      if (currentStatus === 'ORDER_FAILED') {
        if (step.id === 'PAYMENT_STATUS') {
          isFailed = true;
          // If failed, we consider this the active (failed) step
          label = 'Payment Failed';
        }
      }

      // Logic to pick correct timestamp for each stage
      // Note: For failed, we might not have a specific 'failed time' col, but we can assume 'now' or last update if needed.
      // For now, sticking to strict columns.

      let timeLabel = '';

      // Time Display Logic
      const formatTime = (t: any) => t ? dayjs(t).format('hh:mm A') : '';

      if (index === 0) {
        // Stage 1: Placed
        timeLabel = formatTime(order.orderTime);
      } else if (index === 1) {
        // Stage 2: On Way
        // Fallback to changeTime if onWayTime is missing but step is completed
        timeLabel = formatTime(order.history?.onWayTime || (isCompleted ? order.history?.changeTime : null));
      } else if (index === 2) {
        // Stage 3: Payment Pending
        timeLabel = formatTime(order.history?.paymentPendingTime || (isCompleted ? order.history?.changeTime : null));
      } else if (index === 3) {
        // Stage 4: Payment Status 
        // Use paymentPendingTime (if prepaid/instant) or confirmedAt (if COD/Delivered) or changeTime
        timeLabel = formatTime(order.history?.paymentPendingTime || order.history?.changeTime);
      } else if (index === 4) {
        // Stage 5: Delivered
        timeLabel = formatTime(order.confirmedAt);
      }

      // Valid Date Check
      if (timeLabel === 'Invalid Date') timeLabel = '';

      // Special handling for Payment Status Step Label (Success Case)
      if (step.id === 'PAYMENT_STATUS') {
        if (isCompleted && !isFailed) {
          label = 'Payment Received';
        }
      }

      return { ...step, completed: isCompleted, current: isCurrent, time: timeLabel, label, failed: isFailed };
    });
  };

  const timeline = getTimeline(orderData);
  const estimatedTime = getEstimatedTime(orderData);

  if (loading) {
    return (
      <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (error || !orderData) {
    return (
      <div className="min-vh-100 bg-white d-flex flex-column align-items-center justify-content-center p-4">
        <div className="w-100" style={{ maxWidth: '400px' }}>

          {/* Animation */}
          <div className="d-flex justify-content-center mb-4">
            <div style={{ width: '200px' }}>
              <Lottie animationData={noOrderAnimation} loop={true} />
            </div>
          </div>

          {/* Text */}
          <div className="text-center mb-5">
            <h3 className="h5 fw-bold text-dark mb-2">Order Not Found</h3>
            <p className="text-muted small">Please ensure the Order ID is correct and try again.</p>
          </div>

          {/* Action Button */}
          <button
            onClick={() => navigate('/ourCustomers/track-entry')}
            className="btn btn-primary w-100 py-3 rounded-pill fw-bold shadow-lg"
            style={{ background: '#dc2626', borderColor: '#dc2626' }} // Red color for error action
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-vh-100 bg-light d-flex flex-column">
      {/* Header */}
      <div className="bg-white p-3 shadow-sm d-flex align-items-center sticky-top z-1">
        <button onClick={() => navigate('/ourCustomers/history')} className="btn btn-link text-dark p-2 me-2 rounded-circle hover-bg-light">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="h6 fw-bold mb-0 text-dark">Track Order</h1>
          <p className="small text-muted mb-0">ID: {orderData.id}</p>
        </div>
      </div>

      <div className="flex-grow-1 overflow-auto">
        {/* Map / Purchase Order Placeholder */}
        <div className="bg-secondary position-relative w-100 overflow-hidden" style={{ height: '14rem' }}>
          <div
            className="position-absolute top-0 start-0 w-100 h-100 opacity-10"
            style={{
              background: 'radial-gradient(#00CFA5 1px, transparent 1px)',
              backgroundSize: '16px 16px',
            }}
          ></div>
          <div className="position-absolute top-50 start-50 translate-middle text-center">
            <motion.div
              animate={{ scale: [1, 1.1, 1] }}
              transition={{ repeat: Infinity, duration: 2 }}
              className="bg-white text-primary p-3 rounded-circle shadow border border-4 border-light mb-2 d-inline-block"
            >
              <FileText size={32} />
            </motion.div>
            <h5 className="text-white fw-bold shadow-sm" style={{ textShadow: '0 2px 4px rgba(0,0,0,0.5)' }}>
              Purchase Order
            </h5>
          </div>

          {/* Purchase Order Button (Overlay) */}
          <div className="position-absolute bottom-0 start-50 translate-middle-x mb-5" style={{ zIndex: 30 }}>
            <button className="btn btn-sm btn-light fw-bold shadow-sm rounded-pill px-4">View Purchase Order</button>
          </div>
        </div>

        {/* Content Card */}
        <div
          className="bg-white p-4 rounded-top-4 position-relative shadow-custom mx-auto"
          style={{
            marginTop: '-1.5rem',
            zIndex: 20,
            boxShadow: '0 -5px 20px rgba(0,0,0,0.05)',
            borderTopLeftRadius: '1.5rem',
            borderTopRightRadius: '1.5rem',
            minHeight: '60vh',
            maxWidth: '600px', // Center logic limit width
          }}
        >
          <div className="bg-light rounded-pill mx-auto mb-4" style={{ width: '3rem', height: '0.375rem' }} />

          {/* Top Info Grid */}
          <div className="row g-3 mb-4">
            <div className="col-6">
              <div className="p-3 bg-light rounded-4 text-center">
                <p className="small text-muted mb-1 text-uppercase fw-bold" style={{ fontSize: '0.65rem' }}>
                  {orderData.status === 'DELIVERED' || orderData.status === 'ORDER_DELIVERED_SUCESSFULLY' ? 'Total Time' : 'Estimated Time'}
                </p>
                <h5 className="h6 fw-bold text-dark mb-0">{estimatedTime}</h5>
              </div>
            </div>
            <div className="col-6">
              <div className="p-3 bg-light rounded-4 text-center">
                <p className="small text-muted mb-1 text-uppercase fw-bold" style={{ fontSize: '0.65rem' }}>
                  Order Number
                </p>
                <h5 className="h6 fw-bold text-dark mb-0">{orderData.id}</h5>
              </div>
            </div>
          </div>

          {/* Vertical Timeline - Centered Content */}
          <div className="position-relative ps-2">
            {/* Timeline Line */}
            <div className="position-absolute bg-light" style={{ left: '19px', top: '0.5rem', bottom: '2rem', width: '2px' }}></div>

            {timeline.map((item: any, index: number) => (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.1 }}
                key={index}
                className="d-flex gap-4 position-relative pb-4"
              >
                <div className="position-relative" style={{ zIndex: 10 }}>
                  {item.failed ? (
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center bg-danger text-white border border-4 border-white shadow-sm"
                      style={{ width: '2.5rem', height: '2.5rem' }}
                    >
                      <XCircle size={16} />
                    </div>
                  ) : item.completed ? (
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center bg-success text-white border border-4 border-white shadow-sm"
                      style={{ width: '2.5rem', height: '2.5rem' }}
                    >
                      <CheckCircle size={16} />
                    </div>
                  ) : (
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center bg-white border border-2 border-light shadow-sm"
                      style={{ width: '2.5rem', height: '2.5rem' }}
                    >
                      <Circle size={16} className="text-secondary opacity-25" />
                    </div>
                  )}
                </div>
                <div className={`pt-1 ${item.completed || item.failed ? 'opacity-100' : 'opacity-50'}`}>
                  <h4 className={`h6 fw-bold mb-0 ${item.failed ? 'text-danger' : item.completed ? 'text-dark' : 'text-muted'}`}>{item.label}</h4>
                  <p className="small text-muted mb-0">{item.time}</p>
                </div>
              </motion.div>
            ))}
          </div>

        </div>
      </div>

      {/* Footer Action */}
      <div className="p-3 bg-white border-top sticky-bottom">
        <button className="btn btn-primary w-100 py-3 rounded-pill fw-bold shadow-sm">View Purchase Order</button>
      </div>
    </div>
  );
};

export default TrackOrderPage;
