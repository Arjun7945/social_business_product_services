
import React, { useState, useRef, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useLocation } from 'react-router-dom';
import { X } from 'lucide-react';
import Lottie from 'lottie-react';
import otpAnimation from '../assets/animations/OTP Verification.json';
import api from '../api';

const OtpPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const mobile = location.state?.mobile || "+91 88888 88888";
    const customerName = location.state?.customerName || "Customer";

    const [otp, setOtp] = useState(['', '', '', '']);
    const [isLoading, setIsLoading] = useState(false);
    const inputRefs = useRef([]);

    const handleChange = (index, value) => {
        if (isNaN(value)) return;
        const newOtp = [...otp];
        newOtp[index] = value;
        setOtp(newOtp);

        // Auto-focus next input
        if (value && index < 3) {
            inputRefs.current[index + 1].focus();
        }
    };

    const handleKeyDown = (index, e) => {
        // Backspace handling
        if (e.key === 'Backspace' && !otp[index] && index > 0) {
            inputRefs.current[index - 1].focus();
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
                navigate('/status?result=success');
            } else {
                navigate('/status?result=fail');
                // Or show error toast
            }
        } catch (error) {
            navigate('/status?result=fail');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-transparent flex items-center justify-center p-4">
            <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-white rounded-3xl shadow-xl w-full max-w-md overflow-hidden relative min-h-[500px] flex flex-col"
            >
                <button onClick={() => navigate('/login')} className="absolute top-4 left-4 p-2 z-10">
                    <X className="w-6 h-6 text-gray-500" />
                </button>

                <div className="pt-8 pb-0 px-8 text-center flex flex-col items-center">
                    <div className="w-48 h-48 mb-2">
                        <Lottie animationData={otpAnimation} loop={true} />
                    </div>
                    <h2 className="text-gray-800 text-lg font-bold mt-4">
                        Welcome {customerName}!
                    </h2>
                    <p className="text-gray-500 font-medium text-sm mt-1 mb-6 tracking-wide">
                        It's good to see you here! ✨
                    </p>

                    <p className="text-gray-600 mb-8">
                        Code is sent to <span className="font-semibold text-gray-900">{mobile}</span>
                    </p>

                    {/* OTP Inputs */}
                    <div className="flex justify-center gap-4 mb-8">
                        {otp.map((digit, index) => (
                            <input
                                key={index}
                                ref={el => inputRefs.current[index] = el}
                                type="text"
                                maxLength={1}
                                value={digit}
                                onChange={(e) => handleChange(index, e.target.value)}
                                onKeyDown={(e) => handleKeyDown(index, e)}
                                className="w-14 h-14 bg-gray-100 rounded-xl text-center text-2xl font-bold text-gray-800 focus:bg-white focus:ring-2 focus:ring-primary outline-none transition-all"
                            />
                        ))}
                    </div>

                    <div className="text-center mb-8">
                        <p className="text-gray-400 text-sm mb-1">Didn't receive code?</p>
                        <button className="text-primary font-bold hover:text-primary-dark">Request again</button>
                    </div>

                    <button
                        className="text-gray-400 text-xs mt-2 hover:text-primary transition-colors"
                        onClick={() => navigate('/login')}
                    >
                        Don't have an account? <span className="text-primary font-bold">Sign Up</span>
                    </button>
                </div>

                <div className="p-8">
                    <button
                        onClick={handleVerify}
                        disabled={isLoading || otp.join('').length < 4}
                        className={`w-full py-4 rounded-xl text-white font-bold text-lg shadow-lg transition-all
                ${isLoading || otp.join('').length < 4
                                ? 'bg-gray-300'
                                : 'bg-primary hover:bg-primary-dark active:scale-95'
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
