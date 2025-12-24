// src/services/SupplementService.ts
import api from '../api/axiosConfig';
import { Supplement, AiRecommendation, PaginatedResponse } from '../types';

class SupplementService {
    private static instance: SupplementService;
    
    public static getInstance(): SupplementService {
        if (!SupplementService.instance) {
            SupplementService.instance = new SupplementService();
        }
        return SupplementService.instance;
    }

    async getSupplements(page: number = 0, size: number = 20): Promise<PaginatedResponse<Supplement>> {
        const response = await api.get('/supplement', {
            params: { page, size }
        });
        return response.data;
    }

    async getSupplementById(id: number): Promise<Supplement> {
        const response = await api.get(`/supplement/${id}`);
        return response.data;
    }

    async searchSupplements(query: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<Supplement>> {
        const response = await api.get('/supplement/search', {
            params: { query, page, size }
        });
        return response.data;
    }

    async getAiRecommendations(query: string): Promise<AiRecommendation[]> {
        const userId = localStorage.getItem('userNo') || 'temp-user-id';
        const response = await api.post('/api/recommend', null, {
            params: { query, userId }
        });
        
        // 백엔드 응답을 프론트엔드 형식으로 변환
        return response.data.map((item: any, index: number) => ({
            id: `${index}-${item.name}`,
            score: item.score / 100,
            metadata: {
                nutNo: index + 1, // 실제로는 백엔드에서 제공해야 함
                nutName: item.name,
                price: 25000, // 임시 가격
                primaryFnclty: item.reason || '건강기능식품',
                rawName: item.reason || '성분 정보',
                shape: '캡슐',
                warning: '주의사항을 확인하세요',
                stock: 100
            }
        }));
    }

    async getSupplementsByCategory(category: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<Supplement>> {
        const response = await api.get('/supplement/category', {
            params: { category, page, size }
        });
        return response.data;
    }
}

export default SupplementService.getInstance();