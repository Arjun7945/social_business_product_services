/* eslint-disable */
import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import Lottie from 'lottie-react';
import trackOrderAnimation from 'app/assets/animations/track_order.json';
import GlobalBackground from '../components/GlobalBackground';

const OrderEntryPage = () => {
    const navigate = useNavigate();
    const [otp, setOtp] = useState(['', '', '', '', '']); // 5 digit input
    const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

    useEffect(() => {
        // Auto-focus first input on mount
        if (inputRefs.current[0]) {
            inputRefs.current[0]?.focus();
        }
    }, []);

    const handleChange = (element: HTMLInputElement, index: number) => {
        if (isNaN(Number(element.value))) return false;

        const newOtp = [...otp];
        newOtp[index] = element.value;
        setOtp(newOtp);

        // Focus next input
        if (element.value !== '' && index < 4) {
            inputRefs.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === 'Backspace') {
            if (otp[index] === '' && index > 0) {
                inputRefs.current[index - 1]?.focus();
            }
        }
    };

    const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
        e.preventDefault();
        const pastedData = e.clipboardData.getData('text').slice(0, 5).replace(/[^0-9]/g, '');

        if (pastedData) {
            const newOtp = [...otp];
            pastedData.split('').forEach((char, i) => {
                if (i < 5) newOtp[i] = char;
            });
            setOtp(newOtp);

            // Focus the box after the last pasted character
            const nextFocusIndex = Math.min(pastedData.length, 4);
            inputRefs.current[nextFocusIndex]?.focus();
        }
    };

    const handleSubmit = () => {
        const orderId = otp.join('');
        if (orderId.length > 0) {
            navigate(`/ourCustomers/track?orderId=${orderId}`);
        }
    };

    // Auto-submit if all fields filled? Optional. For now, button click.

    return (
        <div className="min-vh-100 d-flex flex-column bg-light position-relative overflow-hidden font-sans">
            <GlobalBackground />

            {/* Header */}
            <div className="p-3 d-flex align-items-center position-relative" style={{ zIndex: 20 }}>
                <button onClick={() => navigate(-1)} className="btn btn-link text-dark p-0">
                    <ArrowLeft size={24} />
                </button>
            </div>

            <div className="flex-grow-1 d-flex flex-column align-items-center justify-content-center p-4 position-relative" style={{ zIndex: 10 }}>

                <div className="w-100" style={{ maxWidth: '400px' }}>
                    <div className="text-center mb-4">
                        <h2 className="h4 fw-bold mb-3">Enter Order ID</h2>
                        <p className="text-muted small">Please enter your Order ID to track its status.</p>
                    </div>

                    <div className="d-flex justify-content-between mb-5 px-2">
                        {otp.map((data, index) => (
                            <input
                                key={index}
                                type="text"
                                inputMode="numeric"
                                pattern="[0-9]*"
                                maxLength={1}
                                ref={el => (inputRefs.current[index] = el)}
                                value={data}
                                onChange={e => handleChange(e.target, index)}
                                onKeyDown={e => handleKeyDown(e, index)}
                                onPaste={handlePaste}
                                className="form-control text-center fw-bold shadow-sm"
                                style={{
                                    width: '3.5rem',
                                    height: '3.5rem',
                                    fontSize: '1.5rem',
                                    borderRadius: '1rem',
                                    border: '1px solid #e2e8f0',
                                    backgroundColor: '#fff'
                                }}
                            />
                        ))}
                    </div>

                    <div className="mb-5 d-flex justify-content-center">
                        <div style={{ width: '250px' }}>
                            <Lottie animationData={trackOrderAnimation} loop={true} />
                        </div>
                    </div>



                </div>
            </div>

            {/* Bottom Button */}
            <div className="p-4 bg-white sticky-bottom border-top">
                <button
                    onClick={handleSubmit}
                    className="btn btn-primary w-100 py-3 rounded-pill fw-bold shadow-lg mb-3"
                    style={{ background: '#6366f1', borderColor: '#6366f1' }} // Indigo color from image
                >
                    Confirm
                </button>
                <button
                    onClick={() => navigate('/ourCustomers/history')}
                    className="btn btn-light w-100 py-3 rounded-pill fw-bold text-muted shadow-sm"
                >
                    Show All Orders
                </button>
            </div>
        </div>
    );
};

export default OrderEntryPage;
