/* eslint-disable */
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
// Check if user exists (Secure Login)
// Returns list of customers matching the phone number
export const checkUserExistence = (mobile: string) => api.get(`/customers?waPhoneNumber.equals=${encodeURIComponent(mobile)}`);

// Add new order endpoints
export const getCustomerOrders = (mobile: string) => api.get(`/customer-orders/by-phone/${encodeURIComponent(mobile)}`);
export const getOrderDetails = (id: string | number) => api.get(`/customer-orders/public/${id}`);
export const getOrderItemsByOrderId = (id: string | number) => api.get(`/customer-orders/public/${id}/items`);

export default api;
