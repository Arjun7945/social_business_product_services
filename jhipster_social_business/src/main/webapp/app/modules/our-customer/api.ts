import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const sendOtp = (mobile: string) => api.post('/otp/send', { mobile });
export const verifyOtp = (mobile: string, code: string) => api.post('/otp/verify', { mobile, code });

// Check if user exists (Secure Login)
// Returns list of customers matching the phone number
export const checkUserExistence = (mobile: string) => api.get(`/customers?waPhoneNumber.equals=${encodeURIComponent(mobile)}`);

export default api;
