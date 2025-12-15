// src/pages/MainPage.tsx
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import { Supplement } from '../types';

const MainPage = () => {
    const [supplements, setSupplements] = useState<Supplement[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchSupplements();
    }, []);

    const fetchSupplements = async () => {
        try {
            // Page 객체로 오므로 .content를 꺼내야 함 (Spring Data JPA 기본 응답 구조)
            const response = await api.get('/supplement?page=0&size=20');
            setSupplements(response.data.content);
        } catch (error) {
            console.error('Failed to fetch supplements', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div className="text-center mt-20">로딩 중...</div>;

    return (
        <div className="max-w-6xl mx-auto py-8">
            {/* 배너 섹션 */}
            <div className="bg-green-50 rounded-2xl p-8 mb-10 flex justify-between items-center">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800 mb-2">내 몸에 딱 맞는 영양제 찾기</h2>
                    <p className="text-gray-600 mb-4">AI 닥터에게 증상을 말하고 추천받아보세요!</p>
                    <Link to="/recommend" className="bg-green-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-green-700">
                        지금 추천받기 →
                    </Link>
                </div>
                <div className="text-6xl">🩺</div>
            </div>

            {/* 상품 리스트 */}
            <h3 className="text-2xl font-bold mb-6 px-4">전체 상품 목록</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 px-4">
                {supplements.map((item) => (
                    <Link key={item.nutNo} to={`/supplement/${item.nutNo}`} className="group">
                        <div className="border rounded-xl overflow-hidden shadow-sm hover:shadow-md transition bg-white">
                            <div className="h-48 bg-gray-100 flex items-center justify-center text-4xl text-gray-300">
                                {/* 이미지가 없으므로 더미 아이콘 표시 */}
                                💊
                            </div>
                            <div className="p-4">
                <span className="text-xs text-green-600 font-bold border border-green-200 px-2 py-0.5 rounded-full">
                  {item.primaryFnclty.split(',')[0] || '건강'}
                </span>
                                <h4 className="font-bold text-lg mt-2 truncate group-hover:text-green-600">
                                    {item.nutName}
                                </h4>
                                <p className="text-gray-500 text-sm mt-1 truncate">{item.rawName}</p>
                                <div className="mt-4 flex justify-between items-center">
                                    <span className="font-bold text-lg">{item.price.toLocaleString()}원</span>
                                </div>
                            </div>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default MainPage;