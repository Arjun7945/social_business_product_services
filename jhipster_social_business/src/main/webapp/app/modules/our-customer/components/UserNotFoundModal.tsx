import React from 'react';
import Lottie from 'lottie-react';

import userNotFoundAnimation from 'app/assets/animations/user_not_found.json';
import { useNavigate } from 'react-router-dom';

interface UserNotFoundModalProps {
  isOpen: boolean;
  onClose: () => void;
  mobileNumber: string;
}

const UserNotFoundModal: React.FC<UserNotFoundModalProps> = ({ isOpen, onClose, mobileNumber }) => {
  const navigate = useNavigate();

  if (!isOpen) return null;

  return (
    <div
      className="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-dark bg-opacity-50"
      style={{ zIndex: 50 }}
    >
      <div className="bg-white rounded-4 shadow-lg p-4 w-100 mx-3" style={{ maxWidth: '24rem' }}>
        {' '}
        {/* max-w-sm -> max-width: 24rem */}
        <div className="d-flex justify-content-center mb-4">
          <div style={{ width: '12rem', height: '12rem' }}>
            <Lottie animationData={userNotFoundAnimation} loop={true} />
          </div>
        </div>
        <h2 className="h4 font-weight-bold text-center mb-2 text-dark">User Not Found</h2>
        <p className="text-center text-muted mb-4">
          Before verifying your OTP, we noticed that
          <span className="fw-bold text-dark mx-1">{mobileNumber}</span>
          is not registered with us.
        </p>
        <div className="d-flex flex-column gap-3">
          <button
            onClick={() => {
              onClose();
              // Navigate to registration or contact support if needed
              // navigate('/register');
            }}
            className="btn btn-primary w-100 py-2 rounded-3 fw-bold"
          >
            Create Account
          </button>

          <button onClick={onClose} className="btn btn-light w-100 py-2 rounded-3 text-muted fw-bold">
            Try Different Number
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserNotFoundModal;
