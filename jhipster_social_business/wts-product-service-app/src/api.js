
import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // Vite proxy handles this to localhost:8080
    headers: {
        'Content-Type': 'application/json',
    },
});

export const sendOtp = (mobile) => api.post('/otp/send', { mobile });
export const verifyOtp = (mobile, code) => api.post('/otp/verify', { mobile, code });

// Check if user exists (Secure Login)
// Returns list of customers matching the phone number
export const checkUserExistence = (mobile) => api.get(`/customers?waPhoneNumber.equals=${encodeURIComponent(mobile)}`);

export default api;
