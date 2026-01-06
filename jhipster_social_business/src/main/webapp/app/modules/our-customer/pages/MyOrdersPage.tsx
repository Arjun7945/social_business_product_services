import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Star } from 'lucide-react';
import { getOrderDetails, getOrderItemsByOrderId } from '../api';
import { IOrderItem } from 'app/shared/model/order-item.model';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';

const MyOrdersPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('orderId');

  const [order, setOrder] = useState<ICustomerOrder | null>(null);
  const [items, setItems] = useState<IOrderItem[]>([]);
  const [loading, setLoading] = useState(true);

  // Rating state (Visual only, defaulting to 0)
  const [rating, setRating] = useState(0);
  const [showRatingGuide, setShowRatingGuide] = useState(true);

  useEffect(() => {
    if (orderId) {
      Promise.all([getOrderDetails(orderId), getOrderItemsByOrderId(orderId)])
        .then(([orderRes, itemsRes]) => {
          setOrder(orderRes.data);
          setItems(itemsRes.data);
          setLoading(false);
        })
        .catch(err => {
          console.error('Failed to load order data', err);
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, [orderId]);

  if (loading) {
    return (
      <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
        <div className="spinner-border text-primary" role="status" />
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-vh-100 d-flex flex-column align-items-center justify-content-center bg-light">
        <p>Order not found</p>
        <button onClick={() => navigate(-1)} className="btn btn-secondary">
          Go Back
        </button>
      </div>
    );
  }

  // Calculations
  const shipping = 20;
  const discount = 20; // displayed as -20

  const subtotal = items.reduce((sum, item) => {
    const price = item.priceAtOrder || item.product?.pricePerKg || 0;
    const qty = item.quantityKg || 0;
    // Assuming priceAtOrder is total for the item OR per unit.
    // Usually priceAtOrder in OrderItem is the price at that time.
    // If it's per Kg, we multiply. If it's total, we check.
    // JHipster usually stores Unit Price.
    // Let's assume Unit Price * Quantity.
    return sum + price * qty;
  }, 0);

  const total = subtotal + shipping - discount;

  return (
    <div className="min-vh-100 bg-light d-flex flex-column">
      {/* Header */}
      <div className="bg-white p-3 shadow-sm d-flex align-items-center sticky-top z-1">
        <button onClick={() => navigate(-1)} className="btn btn-link text-dark p-2 me-2 rounded-circle hover-bg-light">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="h5 fw-bold mb-0 text-dark">My Orders</h1>
        </div>
      </div>

      <div className="flex-grow-1 p-3 overflow-auto">
        <div className="d-flex flex-column gap-3" style={{ maxWidth: '600px', margin: '0 auto' }}>
          {/* Items List */}
          {items.map((item, index) => (
            <div key={index} className="bg-white p-3 rounded-4 shadow-sm d-flex gap-3 align-items-center">
              {/* Image */}
              <div
                className="bg-light rounded-3 d-flex align-items-center justify-content-center overflow-hidden"
                style={{ width: '80px', height: '80px', flexShrink: 0 }}
              >
                {item.product?.image?.imageUrl ? (
                  <img src={item.product?.image?.imageUrl} alt={item.product?.name} className="w-100 h-100 object-fit-cover" />
                ) : (
                  <span className="text-muted small">No Img</span>
                )}
              </div>

              {/* Details */}
              <div className="flex-grow-1">
                <h5 className="h6 fw-bold mb-1">{item.product?.name || 'Unknown Product'}</h5>
                <p className="small text-muted mb-2">{item.product?.description || 'Single'}</p>

                <div className="d-flex align-items-center justify-content-between">
                  {/* Quantity & Unit Price */}
                  <div className="d-flex flex-column">
                    <div className="border rounded px-3 py-1 bg-white text-dark small fw-bold text-center">{item.quantityKg} KG</div>
                    <span className="text-muted tiny mt-1" style={{ fontSize: '0.7rem' }}>
                      ₹{item.priceAtOrder || item.product?.pricePerKg} / kg
                    </span>
                  </div>

                  {/* Total Item Price */}
                  <span className="fw-bold">₹{(item.priceAtOrder * item.quantityKg).toFixed(2)}</span>
                </div>
              </div>
            </div>
          ))}

          {/* Empty State if no items */}
          {items.length === 0 && <div className="text-center py-5 text-muted">No items found for this order.</div>}

          {/* Calculations Card */}
          <div className="bg-white p-3 rounded-4 shadow-sm mt-2">
            <div className="d-flex justify-content-between mb-2">
              <span className="text-muted">Subtotal</span>
              <span className="fw-bold">₹{subtotal.toFixed(2)}</span>
            </div>
            <div className="d-flex justify-content-between mb-2">
              <span className="text-muted">Shipping</span>
              <span className="fw-bold">₹{shipping.toFixed(2)}</span>
            </div>
            <div className="d-flex justify-content-between mb-2">
              <span className="text-success small">Discounts applied</span>
              <span className="text-success small fw-bold">-₹{discount.toFixed(2)}</span>
            </div>

            {/* Darker Separator */}
            <div className="my-2 border-bottom border-secondary opacity-100"></div>

            <div className="d-flex justify-content-between mt-2">
              <span className="h6 fw-bold mb-0">Total price</span>
              <span className="h5 fw-bold mb-0">₹{total.toFixed(2)}</span>
            </div>
          </div>

          {/* Rating Section with Guide Message */}
          <div className="bg-white p-4 rounded-4 shadow-sm mt-3 text-center position-relative">
            {/* Guide Message Pop-up */}
            {showRatingGuide && rating === 0 && (
              <div
                className="position-absolute bg-primary text-white p-2 rounded shadow-lg"
                style={{ top: '-40px', left: '50%', transform: 'translateX(-50%)', whiteSpace: 'nowrap', zIndex: 10, fontSize: '0.8rem' }}
              >
                Liked the product? Give us a star!
                <div
                  style={{
                    position: 'absolute',
                    top: '100%',
                    left: '50%',
                    transform: 'translateX(-50%)',
                    borderLeft: '6px solid transparent',
                    borderRight: '6px solid transparent',
                    borderTop: '6px solid #0d6efd',
                  }}
                />
              </div>
            )}

            <h5 className="fw-bold mb-3">Rate this product</h5>
            <div className="d-flex justify-content-center gap-2">
              {[1, 2, 3, 4, 5].map(star => (
                <Star
                  key={star}
                  size={32}
                  fill={star <= rating ? '#FFD700' : 'none'} // Gold filled
                  color={star <= rating ? '#FFD700' : '#D1D5DB'} // Gold or Gray stroke
                  className="cursor-pointer"
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    setRating(star);
                    setShowRatingGuide(false);
                  }}
                />
              ))}
            </div>
          </div>

          {/* Spacing for bottom */}
          <div className="mb-4"></div>
        </div>
      </div>
    </div>
  );
};

export default MyOrdersPage;
