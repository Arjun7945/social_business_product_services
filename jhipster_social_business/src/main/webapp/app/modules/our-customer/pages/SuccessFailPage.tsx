import React from 'react';
import { motion } from 'framer-motion';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { X } from 'lucide-react';
import Lottie from 'lottie-react';

import successAnimation from 'app/assets/animations/success_Verification.json';

import failedAnimation from 'app/assets/animations/failed_Verification.json';

const SuccessFailPage = () => {
  const [searchParams] = useSearchParams();
  const result = searchParams.get('result');
  const navigate = useNavigate();
  const isSuccess = result === 'success';

  return (
    <div
      className="min-vh-100 position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center p-4 bg-black bg-opacity-50"
      style={{ zIndex: 1050, backdropFilter: 'blur(4px)' }}
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        className="bg-white rounded-4 p-4 p-md-5 w-100 shadow-lg position-relative text-center"
        style={{ maxWidth: '24rem' }}
      >
        <button
          onClick={() => navigate(isSuccess ? '/ourCustomers/track-entry' : '/ourCustomers/otp')}
          className="position-absolute top-0 end-0 m-3 p-2 border-0 bg-transparent rounded-circle hover-bg-light"
          style={{ zIndex: 10 }}
        >
          <X className="text-secondary" size={20} />
        </button>

        <div className="d-flex justify-content-center mb-4">
          <div style={{ width: '8rem', height: '8rem' }}>
            {isSuccess ? <Lottie animationData={successAnimation} loop={true} /> : <Lottie animationData={failedAnimation} loop={true} />}
          </div>
        </div>

        <h3 className="h5 fw-bold text-dark mb-2">{isSuccess ? 'Your account login' : 'Login Failed'}</h3>

        <p className={`h5 fw-bold mb-4 ${isSuccess ? 'text-primary' : 'text-danger'}`}>{isSuccess ? 'SUCCESSFUL!' : 'PLEASE RETRY'}</p>

        {isSuccess && <p className="text-muted small mb-4 text-uppercase letter-spacing-wide">click ok & enjoy</p>}

        <button
          onClick={() => navigate(isSuccess ? '/ourCustomers/track-entry' : '/ourCustomers/otp')}
          className={`btn w-100 py-3 rounded-4 fw-bold fs-5 shadow transition-all ${isSuccess ? 'btn-primary' : 'btn-danger'}`}
        >
          {isSuccess ? 'OK' : 'TRY AGAIN'}
        </button>
      </motion.div>
    </div>
  );
};

export default SuccessFailPage;
