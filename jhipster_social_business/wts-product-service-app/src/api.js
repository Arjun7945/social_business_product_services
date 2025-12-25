
import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // Vite proxy handles this to localhost:8080
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;
