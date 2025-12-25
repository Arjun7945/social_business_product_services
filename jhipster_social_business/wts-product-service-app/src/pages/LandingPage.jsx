
import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

const LandingPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gradient-to-br from-white to-gray-100 flex flex-col items-center justify-center p-4">
            {/* Brand Logo Animation */}
            <motion.div
                initial={{ opacity: 0, scale: 0.5 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{
                    duration: 1.2,
                    ease: [0, 0.71, 0.2, 1.01],
                    scale: {
                        type: "spring",
                        damping: 10,
                        stiffness: 100,
                        restDelta: 0.001
                    }
                }}
                className="mb-12"
            >
                <img
                    src="/brandlogo.png"
                    alt="Team Together"
                    className="w-48 h-auto object-contain drop-shadow-2xl"
                />
            </motion.div>

            {/* Get Started Button */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.8, duration: 0.5 }}
            >
                <button
                    onClick={() => navigate('/login')}
                    className="bg-primary hover:bg-primary-dark text-white text-lg font-semibold py-4 px-12 rounded-full shadow-lg hover:shadow-xl transform hover:-translate-y-1 transition-all duration-300 active:scale-95"
                >
                    Get Started
                </button>
            </motion.div>

            {/* Footer Text */}
            <motion.p
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 1.5, duration: 1 }}
                className="absolute bottom-8 text-gray-400 text-sm"
            >
                Experience Premium Delivery
            </motion.p>
        </div>
    );
};

export default LandingPage;
