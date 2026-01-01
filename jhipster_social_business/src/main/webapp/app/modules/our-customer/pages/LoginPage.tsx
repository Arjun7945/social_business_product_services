import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { X } from 'lucide-react';
import { checkUserExistence } from '../api';
import Lottie from 'lottie-react';
import UserNotFoundModal from '../components/UserNotFoundModal';

import loginAnimation from 'app/assets/animations/Login.json';

const LoginPage = () => {
  const navigate = useNavigate();
  const [mobile, setMobile] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showNotFoundModal, setShowNotFoundModal] = useState(false);

  // Function to simulate sending OTP (Phase 1 Mock or Real API)
  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!mobile || mobile.length < 10) {
      alert('Please enter a valid mobile number');
      return;
    }

    setIsLoading(true);
    try {
      // 1. Secure Login Check: Does user exist?
      // Note: DB seems to store format as 919497144795 (without +).
      // So we prepend 91 only.
      const fullMobile = `91${mobile.trim()}`;
      const checkRes = await checkUserExistence(fullMobile);

      const customers = checkRes.data;

      if (!customers || customers.length === 0) {
        // Open Custom Modal instead of Alert
        setShowNotFoundModal(true);
        setIsLoading(false);
        return;
      }

      // 2. User Found!
      const customerName = customers[0].name;

      // 3. Send OTP (Mock or Real)
      // await sendOtp(mobile); // Uncomment when SMS gateway is active

      // 4. Navigate to OTP with state
      navigate('/ourCustomers/otp', {
        state: {
          mobile,
          customerName,
        },
      });
    } catch (error) {
      console.error('Login Error', error);
      alert('Login Failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-vh-100 bg-transparent d-flex align-items-center justify-content-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white rounded-4 shadow-lg w-100 position-relative overflow-hidden"
        style={{ maxWidth: '28rem' }}
      >
        {/* Close Button Mockup */}
        <button
          className="position-absolute top-0 start-0 m-3 p-2 border-0 bg-transparent rounded-circle hover-bg-light"
          style={{ zIndex: 10 }}
        >
          <X className="text-secondary" size={24} />
        </button>

        {/* Header Section with Lottie Animation */}
        <div
          className="pt-4 pb-2 px-4 text-center d-flex flex-column align-items-center"
          style={{ background: 'linear-gradient(to bottom, rgba(var(--bs-primary-rgb), 0.1), transparent)' }}
        >
          <div style={{ width: '12rem', height: '12rem', marginBottom: '0.5rem' }}>
            <Lottie animationData={loginAnimation} loop={true} />
          </div>
        </div>

        {/* Input Section */}
        <div className="p-4 pt-2">
          <form onSubmit={handleSendOtp}>
            <label className="d-block text-secondary small fw-bold mb-2 ms-1">Enter your registered whatsapp mobile number</label>
            <div className="d-flex align-items-center gap-3 mb-4 border-bottom border-2 border-light focus-within-border-primary pb-2 transition-all">
              <span className="fs-5 fw-semibold text-muted">+91</span>
              <input
                type="tel"
                value={mobile}
                onChange={e => setMobile(e.target.value.replace(/\D/g, '').slice(0, 10))}
                placeholder="1234567890"
                className="flex-grow-1 border-0 fs-5 fw-semibold text-dark outline-none bg-transparent"
                style={{ outline: 'none', boxShadow: 'none' }}
                autoFocus
              />
            </div>

            <button
              type="submit"
              disabled={isLoading || mobile.length < 10}
              className={`btn w-100 py-3 rounded-4 fs-5 fw-bold d-flex align-items-center justify-content-center gap-2 transition-all shadow-sm ${
                isLoading || mobile.length < 10 ? 'btn-secondary disabled opacity-50' : 'btn-primary hover-shadow-lg active-scale-95'
              }`}
            >
              {isLoading ? 'Sending...' : 'CONTINUE'}
            </button>
          </form>
        </div>
      </motion.div>
      {/* User Not Found Modal */}
      <UserNotFoundModal isOpen={showNotFoundModal} onClose={() => setShowNotFoundModal(false)} mobileNumber={mobile} />
    </div>
  );
};

export default LoginPage;
