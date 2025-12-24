// src/pages/MainPage.tsx
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import { Supplement } from '../types';
import ProductCard from '../components/ProductCard';
import LoadingSpinner from '../components/LoadingSpinner';

const MainPage = () => {
    const [supplements, setSupplements] = useState<Supplement[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchSupplements();
    }, []);

    const fetchSupplements = async () => {
        try {
            const response = await api.get('/supplement?page=0&size=20');
            setSupplements(response.data.content);
        } catch (error) {
            console.error('Failed to fetch supplements', error);
        } finally {
            setLoading(false);
        }
    };

    // 카테고리 목록 (Airbnb 스타일 아이콘 필터)
    const categories = [
        { id: 'all', icon: '🏠', label: '전체' },
        { id: 'vitamin', icon: '💊', label: '비타민' },
        { id: 'probiotics', icon: '🦠', label: '유산균' },
        { id: 'omega3', icon: '🐟', label: '오메가3' },
        { id: 'eye', icon: '👀', label: '눈건강' },
        { id: 'liver', icon: '🍷', label: '간건강' },
        { id: 'bone', icon: '🦴', label: '뼈/관절' },
        { id: 'energy', icon: '⚡', label: '활력' },
    ];

    const [selectedCategory, setSelectedCategory] = useState('all');

    if (loading) return <LoadingSpinner message="상품을 불러오는 중..." />;

    return (
        <div className="min-h-screen bg-white">
            {/* 카테고리 필터 바 (Sticky Header) */}
            <div className="sticky top-16 z-40 bg-white border-b border-gray-100 shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center space-x-8 overflow-x-auto py-4 scrollbar-hide">
                        {categories.map((category) => (
                            <button
                                key={category.id}
                                onClick={() => setSelectedCategory(category.id)}
                                className={`flex flex-col items-center min-w-[64px] cursor-pointer group transition-all duration-200 ${selectedCategory === category.id
                                        ? 'text-gray-900 border-b-2 border-gray-900 pb-1' // 선택됨
                                        : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50 border-b-2 border-transparent pb-1' // 기본
                                    }`}
                            >
                                <span className={`text-2xl mb-1 ${selectedCategory === category.id ? 'scale-110' : 'group-hover:scale-110'} transition-transform`}>
                                    {category.icon}
                                </span>
                                <span className="text-xs font-semibold whitespace-nowrap">
                                    {category.label}
                                </span>
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            <div className="w-full px-4 sm:px-6 lg:px-8 py-6">
                {/* 상품 리스트 섹션 */}
                <div className="max-w-7xl mx-auto">
                    {supplements.length > 0 ? (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-x-6 gap-y-10">
                            {supplements.map((supplement) => (
                                <ProductCard key={supplement.nutNo} supplement={supplement} />
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-32">
                            <div className="text-6xl mb-4">📭</div>
                            <h3 className="text-lg font-medium text-gray-900">등록된 상품이 없습니다</h3>
                            <p className="text-gray-500 mt-1">관리자에게 데이터 추가를 요청해보세요.</p>
                        </div>
                    )}
                </div>
            </div>

            {/* 플로팅 추천 버튼 (히어로 배너 대체) */}
            <Link
                to="/recommend"
                className="fixed bottom-8 left-1/2 transform -translate-x-1/2 bg-gray-900 text-white px-6 py-3 rounded-full font-semibold shadow-2xl hover:scale-105 transition-transform z-50 flex items-center gap-2"
            >
                <span>AI 맞춤 추천받기</span>
                <span className="text-accent-500">✨</span>
            </Link>
        </div>
    );
};

export default MainPage;