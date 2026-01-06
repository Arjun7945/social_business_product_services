import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

import brandLogo from '../../../../content/images/brandlogo.png';

const LandingPage = () => {
  const navigate = useNavigate();

  return (
    <div className="min-vh-100 d-flex flex-column align-items-center justify-content-center p-4">
      {/* Brand Logo Animation */}
      <motion.div
        initial={{ opacity: 0, scale: 0.5 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{
          duration: 1.2,
          ease: [0, 0.71, 0.2, 1.01],
          scale: {
            type: 'spring',
            damping: 10,
            stiffness: 100,
            restDelta: 0.001,
          },
        }}
        className="mb-5"
      >
        <img
          src={brandLogo}
          alt="Team Together"
          className="img-fluid drop-shadow"
          style={{
            width: '12rem',
            height: 'auto',
            filter: 'drop-shadow(0 10px 8px rgb(0 0 0 / 0.04)) drop-shadow(0 4px 3px rgb(0 0 0 / 0.1))',
          }}
        />
      </motion.div>

      {/* Get Started Button */}
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.8, duration: 0.5 }}>
        <button
          onClick={() => navigate('/ourCustomers/login')}
          className="btn btn-primary rounded-pill shadow-lg text-white fw-bold fs-5 px-5 py-3 transition-all hover-transform-up active-scale-95"
          style={{ paddingLeft: '3rem', paddingRight: '3rem' }}
        >
          Get Started
        </button>
      </motion.div>

      {/* Footer Text */}
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 1.5, duration: 1 }}
        className="position-absolute bottom-0 mb-4 text-muted small"
      >
        Experience Premium Delivery
      </motion.p>
    </div>
  );
};

export default LandingPage;
