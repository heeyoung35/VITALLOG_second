// src/api/axiosConfig.ts
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080', // 백엔드 주소 (application.yml 확인 필요)
    headers: {
        'Content-Type': 'application/json',
    },
});

// 요청 인터셉터: 토큰이 있으면 헤더에 추가
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;