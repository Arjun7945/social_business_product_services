
import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Check, X } from 'lucide-react';

const SuccessFailPage = () => {
    const [searchParams] = useSearchParams();
    const result = searchParams.get('result');
    const navigate = useNavigate();
    const isSuccess = result === 'success';

    return (
        <div className="min-h-screen bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 fixed inset-0 z-50">
            <motion.div
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-white rounded-3xl p-8 w-full max-w-sm text-center shadow-2xl relative"
            >
                <button
                    onClick={() => navigate(isSuccess ? '/track' : '/otp')}
                    className="absolute top-4 right-4 p-2 rounded-full hover:bg-gray-100"
                >
                    <X className="w-5 h-5 text-gray-400" />
                </button>

                <div className="flex justify-center mb-6">
                    <motion.div
                        initial={{ scale: 0 }}
                        animate={{ scale: 1 }}
                        transition={{ type: "spring", stiffness: 200, damping: 10, delay: 0.2 }}
                        className={`w-24 h-24 rounded-full flex items-center justify-center ${isSuccess ? 'bg-green-100' : 'bg-red-100'}`}
                    >
                        {isSuccess ? (
                            <div className="bg-primary rounded-full p-2 relative">
                                {/* Shield Icon Mockup */}
                                <Check className="w-10 h-10 text-white" />
                            </div>
                        ) : (
                            <div className="bg-red-500 rounded-full p-2">
                                <X className="w-10 h-10 text-white" />
                            </div>
                        )}
                    </motion.div>
                </div>

                <h3 className="text-xl font-bold text-gray-800 mb-2">
                    {isSuccess ? 'Your account login' : 'Login Failed'}
                </h3>

                <p className={`text-lg font-bold mb-6 ${isSuccess ? 'text-primary' : 'text-red-500'}`}>
                    {isSuccess ? 'SUCCESSFUL!' : 'PLEASE RETRY'}
                </p>

                {isSuccess && (
                    <p className="text-gray-400 text-sm mb-8 uppercase tracking-widest">click ok & enjoy</p>
                )}

                <button
                    onClick={() => navigate(isSuccess ? '/track' : '/otp')}
                    className={`w-full py-4 rounded-xl text-white font-bold text-lg shadow-lg transition-all
            ${isSuccess
                            ? 'bg-primary hover:bg-primary-dark hover:shadow-xl'
                            : 'bg-red-500 hover:bg-red-600'
                        }`}
                >
                    {isSuccess ? 'OK' : 'TRY AGAIN'}
                </button>
            </motion.div>
        </div>
    );
};

export default SuccessFailPage;
