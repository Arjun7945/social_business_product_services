import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { X } from 'lucide-react';
import Lottie from 'lottie-react';
import loginAnimation from '../assets/animations/Login.json';

const LoginPage = () => {
    const navigate = useNavigate();
    const [mobile, setMobile] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    // Function to simulate sending OTP (Phase 1 Mock or Real API)
    const handleSendOtp = async (e) => {
        e.preventDefault();
        if (!mobile || mobile.length < 10) return;

        setIsLoading(true);
        try {
            // For Phase 1 Demo (Simulated delay):
            await new Promise(resolve => setTimeout(resolve, 1000));
            navigate('/otp', { state: { mobile: `+91${mobile}` } });
        } catch (error) {
            console.error("Failed to send OTP", error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-white rounded-3xl shadow-xl w-full max-w-md overflow-hidden relative"
            >
                {/* Close Button Mockup */}
                <button className="absolute top-4 left-4 p-2 rounded-full hover:bg-gray-100 z-10">
                    <X className="w-6 h-6 text-gray-500" />
                </button>

                {/* Header Section with Lottie Animation */}
                <div className="pt-8 pb-4 px-8 text-center bg-gradient-to-b from-primary-light/10 to-transparent flex flex-col items-center">
                    <div className="w-48 h-48 mb-2">
                        <Lottie animationData={loginAnimation} loop={true} />
                    </div>
                </div>

                {/* Input Section */}
                <div className="p-8 pt-4">
                    <form onSubmit={handleSendOtp}>
                        <label className="block text-gray-700 text-sm font-bold mb-2 ml-1">
                            Enter your mobile number
                        </label>
                        <div className="flex items-center gap-3 mb-8">
                            <span className="text-lg font-semibold text-gray-500">+91</span>
                            <input
                                type="tel"
                                value={mobile}
                                onChange={(e) => setMobile(e.target.value.replace(/\D/g, '').slice(0, 10))}
                                placeholder="1234567890"
                                className="flex-1 text-lg font-semibold text-gray-900 placeholder-gray-300 border-b-2 border-gray-200 focus:border-primary outline-none py-2 transition-colors"
                                autoFocus
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading || mobile.length < 10}
                            className={`w-full py-4 rounded-xl text-white font-bold text-lg shadow-lg flex items-center justify-center gap-2 transition-all
                ${isLoading || mobile.length < 10
                                    ? 'bg-gray-300 cursor-not-allowed'
                                    : 'bg-primary hover:bg-primary-dark hover:shadow-xl active:scale-95'
                                }`}
                        >
                            {isLoading ? 'Sending...' : 'CONTINUE'}
                        </button>
                    </form>
                </div>
            </motion.div>
        </div>
    );
};

export default LoginPage;
