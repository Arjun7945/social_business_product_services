
import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X } from 'lucide-react';
import Lottie from 'lottie-react';
import notFoundAnimation from '../assets/animations/user_not_found.json';

const UserNotFoundModal = ({ isOpen, onClose, mobileNumber }) => {
    return (
        <AnimatePresence>
            {isOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    {/* Backdrop */}
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={onClose}
                        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
                    />

                    {/* Modal Content */}
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        exit={{ scale: 0.9, opacity: 0 }}
                        className="relative bg-white rounded-3xl p-8 w-full max-w-sm shadow-2xl overflow-hidden"
                    >
                        {/* Close Button */}
                        <button
                            onClick={onClose}
                            className="absolute top-4 right-4 p-2 rounded-full hover:bg-gray-100 transition-colors"
                        >
                            <X className="w-5 h-5 text-gray-500" />
                        </button>

                        <div className="flex flex-col items-center text-center">
                            {/* Animation */}
                            <div className="w-40 h-40 mb-4">
                                <Lottie animationData={notFoundAnimation} loop={true} />
                            </div>

                            {/* Title */}
                            <h3 className="text-xl font-bold text-gray-900 mb-2">
                                Checking Your Number
                            </h3>

                            {/* Message */}
                            <p className="text-gray-600 text-sm leading-relaxed mb-6">
                                The number <span className="font-bold text-gray-900">{mobileNumber}</span> is not registered.<br />
                                <br />
                                Please check the number again or try a different one. Make sure you use the same <span className="font-semibold text-green-600">WhatsApp Number</span> you registered with us.
                            </p>

                            {/* Action Button */}
                            <button
                                onClick={onClose}
                                className="w-full bg-gray-100 hover:bg-gray-200 text-gray-900 font-semibold py-3.5 rounded-xl transition-all outline-none focus:ring-2 focus:ring-gray-300"
                            >
                                Try Again
                            </button>
                        </div>
                    </motion.div>
                </div>
            )}
        </AnimatePresence>
    );
};

export default UserNotFoundModal;
