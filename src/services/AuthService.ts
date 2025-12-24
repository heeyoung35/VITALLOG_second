// src/services/AuthService.ts
import api from '../api/axiosConfig';
import { LoginRequest, LoginResponse } from '../types';

class AuthService {
    private static instance: AuthService;
    
    public static getInstance(): AuthService {
        if (!AuthService.instance) {
            AuthService.instance = new AuthService();
        }
        return AuthService.instance;
    }

    async login(credentials: LoginRequest): Promise<LoginResponse> {
        const requestData = { userId: credentials.id, pwd: credentials.pw };
        const response = await api.post('/api/user/login', requestData);
        const { token } = response.data;
        
        localStorage.setItem('accessToken', token);
        
        return response.data;
    }

    async signUp(credentials: LoginRequest): Promise<void> {
        const requestData = { userId: credentials.id, pwd: credentials.pw };
        await api.post('/api/user/signup', requestData);
    }

    logout(): void {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/';
    }

    isAuthenticated(): boolean {
        return !!localStorage.getItem('accessToken');
    }

    getToken(): string | null {
        return localStorage.getItem('accessToken');
    }

    async refreshToken(): Promise<string | null> {
        try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (!refreshToken) return null;

            const response = await api.post('/auth/refresh', { refreshToken });
            const { accessToken } = response.data;
            
            localStorage.setItem('accessToken', accessToken);
            return accessToken;
        } catch (error) {
            this.logout();
            return null;
        }
    }
}

export default AuthService.getInstance();