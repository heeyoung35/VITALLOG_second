// src/pages/AiRecommendPage.tsx
import { useState } from 'react';
import api from '../api/axiosConfig';
import { AiRecommendation } from '../types';

const AiRecommendPage = () => {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState<AiRecommendation[]>([]);
    const [loading, setLoading] = useState(false);

    const handleSearch = async () => {
        if (!query) return;
        setLoading(true);
        try {
            // SupplementVectorController의 검색 API 호출 (경로 확인 필요)
            // 예: /api/vector/search?query=피로회복
            const response = await api.get(`/api/vector/search`, {
                params: { query: query }
            });
            setResults(response.data); // RankedResponseDTO 리스트 반환 가정
        } catch (error) {
            console.error("추천 실패", error);
            alert("AI 추천을 불러오지 못했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto mt-10">
            <h1 className="text-3xl font-bold mb-6 text-center">AI 영양제 추천 닥터 💊</h1>

            <div className="flex gap-2 mb-8">
                <input
                    type="text"
                    className="flex-1 border p-3 rounded-lg shadow-sm"
                    placeholder="증상을 자세히 입력해주세요 (예: 요즘 잠을 못 자고 피곤해)"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                />
                <button
                    onClick={handleSearch}
                    disabled={loading}
                    className="bg-green-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-green-700 transition"
                >
                    {loading ? '분석 중...' : '추천받기'}
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {results.map((item) => (
                    <div key={item.id} className="border p-4 rounded-xl shadow hover:shadow-lg transition">
                        <div className="flex justify-between items-start">
                            <h3 className="text-xl font-bold text-gray-800">{item.metadata.name}</h3>
                            <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                일치도 {(item.score * 100).toFixed(0)}%
              </span>
                        </div>
                        <p className="text-gray-600 mt-2">{item.metadata.description}</p>
                        <p className="text-lg font-bold mt-4 text-green-600">
                            {item.metadata.price.toLocaleString()}원
                        </p>
                    </div>
                ))}
            </div>

            {!loading && results.length === 0 && (
                <p className="text-center text-gray-500">증상을 입력하면 AI가 딱 맞는 영양제를 찾아드립니다.</p>
            )}
        </div>
    );
};

export default AiRecommendPage;
