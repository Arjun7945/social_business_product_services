/* eslint-disable */
import React, { useState, useRef } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useLocation } from 'react-router-dom';
import { X } from 'lucide-react';
import Lottie from 'lottie-react';

import otpAnimation from 'app/assets/animations/OTP Verification.json';
// import api from '../api'; // Uncomment for real API

const OtpPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const mobileProp = location.state?.mobile;
  const mobile = mobileProp || '+91 88888 88888';
  const customerName = location.state?.customerName || 'Customer';

  const [otp, setOtp] = useState(['', '', '', '']);
  const [isLoading, setIsLoading] = useState(false);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const handleChange = (index: number, value: string) => {
    if (isNaN(Number(value))) return;
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // Auto-focus next input
    if (value && index < 3) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    // Backspace handling
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handleVerify = async () => {
    const code = otp.join('');
    if (code.length < 4) return;

    setIsLoading(true);
    try {
      // Mock API call
      // const response = await api.post('/otp/verify', { mobile, code });

      // Simulation:
      await new Promise(resolve => setTimeout(resolve, 1500));

      // Mock Success (Any code 1234 succeeds, others fail for demo)
      if (code === '1234') {
        // Save mobile to localStorage for History page
        // Ensure we save the format matching the DB (with 91 prefix)
        const savedMobile = mobileProp && mobileProp.length === 10 ? `91${mobileProp}` : mobile;
        localStorage.setItem('customerMobile', savedMobile);

        navigate('/ourCustomers/status?result=success');
      } else {
        navigate('/ourCustomers/status?result=fail');
        // Or show error toast
      }
    } catch (error) {
      navigate('/ourCustomers/status?result=fail');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-vh-100 bg-transparent d-flex align-items-center justify-content-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="bg-white rounded-4 shadow-lg w-100 position-relative overflow-hidden d-flex flex-column"
        style={{ maxWidth: '28rem', minHeight: '500px' }}
      >
        <button
          onClick={() => navigate('/ourCustomers/login')}
          className="position-absolute top-0 start-0 m-3 p-2 border-0 bg-transparent rounded-circle hover-bg-light"
          style={{ zIndex: 10 }}
        >
          <X className="text-secondary" size={24} />
        </button>

        <div className="pt-5 pb-0 px-4 text-center d-flex flex-column align-items-center">
          <div style={{ width: '12rem', height: '12rem', marginBottom: '0.5rem' }}>
            <Lottie animationData={otpAnimation} loop={true} />
          </div>
          <h2 className="text-dark h5 fw-bold mt-3">Welcome {customerName}!</h2>
          <p className="text-secondary fw-medium small mt-1 mb-4 tracking-wide">It&apos;s good to see you here! ✨</p>

          <p className="text-muted mb-4">
            Code is sent to <span className="fw-bold text-dark">{mobile}</span>
          </p>

          {/* OTP Inputs */}
          <div className="d-flex justify-content-center gap-3 mb-4">
            {otp.map((digit, index) => (
              <input
                key={index}
                ref={el => {
                  inputRefs.current[index] = el;
                }}
                type="text"
                maxLength={1}
                value={digit}
                onChange={e => handleChange(index, e.target.value)}
                onKeyDown={e => handleKeyDown(index, e)}
                className="form-control text-center fs-2 fw-bold text-dark"
                style={{ width: '3.5rem', height: '3.5rem', borderRadius: '0.75rem', backgroundColor: '#f3f4f6' }}
              />
            ))}
          </div>

          <div className="text-center mb-4">
            <p className="text-muted small mb-1">Didn&apos;t receive code?</p>
            <button className="btn btn-link text-decoration-none text-primary fw-bold p-0">Request again</button>
          </div>

          <button
            className="btn btn-link text-decoration-none text-secondary small mt-2 hover-text-primary transition-colors"
            onClick={() => navigate('/ourCustomers/login')}
          >
            Don&apos;t have an account? <span className="text-primary fw-bold">Sign Up</span>
          </button>
        </div>

        <div className="p-4 mt-auto">
          <button
            onClick={handleVerify}
            disabled={isLoading || otp.join('').length < 4}
            className={`btn w-100 py-3 rounded-4 fs-5 fw-bold d-flex align-items-center justify-content-center shadow-lg transition-all ${isLoading || otp.join('').length < 4 ? 'btn-secondary disabled opacity-50' : 'btn-primary hover-shadow-xl active-scale-95'
              }`}
          >
            {isLoading ? 'Verifying...' : 'LOGIN'}
          </button>
        </div>
      </motion.div>
    </div>
  );
};

export default OtpPage;
