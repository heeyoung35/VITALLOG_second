// src/pages/AiRecommendPage.tsx
import { useState } from 'react';
import api from '../api/axiosConfig';
import { AiRecommendation } from '../types';
import ProductCard from '../components/ProductCard';

const AiRecommendPage = () => {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState<AiRecommendation[]>([]);
    const [loading, setLoading] = useState(false);
    const [hasSearched, setHasSearched] = useState(false);

    const exampleQueries = [
        "요즘 잠을 못 자고 피곤해요",
        "눈이 자주 피로하고 건조해요",
        "면역력이 떨어진 것 같아요",
        "관절이 아프고 뻣뻣해요",
        "소화가 잘 안되고 속이 불편해요"
    ];

    const handleSearch = async (searchQuery?: string) => {
        const searchText = searchQuery || query;
        if (!searchText.trim()) {
            alert('증상을 입력해주세요');
            return;
        }
        
        setLoading(true);
        setHasSearched(true);
        
        try {
            // 임시 사용자 ID (실제로는 로그인한 사용자 정보에서 가져와야 함)
            const userId = localStorage.getItem('userId') || 'temp-user-id';
            const response = await api.post('/api/recommend', null, {
                params: { 
                    query: searchText,
                    userId: userId
                }
            });
            
            // 백엔드 응답 구조에 맞게 변환
            const transformedResults = response.data.map((item: any, index: number) => ({
                id: `${index}-${item.name}`,
                score: item.score / 100, // 백엔드에서 0-100 범위로 오는 것을 0-1로 변환
                metadata: {
                    nutNo: index + 1, // 실제로는 백엔드에서 nutNo를 제공해야 함
                    nutName: item.name,
                    price: 25000, // 임시 가격 (실제로는 백엔드에서 제공)
                    primaryFnclty: item.reason || '건강기능식품',
                    rawName: item.reason || '성분 정보',
                    shape: '캡슐',
                    warning: '주의사항을 확인하세요',
                    stock: 100
                }
            }));
            
            setResults(transformedResults);
        } catch (error: any) {
            console.error("추천 실패", error);
            const message = error.response?.data?.message || "AI 추천을 불러오지 못했습니다.";
            alert(message);
            setResults([]);
        } finally {
            setLoading(false);
        }
    };

    const handleExampleClick = (example: string) => {
        setQuery(example);
        handleSearch(example);
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-neutral-cream via-white to-primary-50">
            <div className="w-full py-8 lg:py-12 px-4 sm:px-6 lg:px-8">
                {/* 헤더 섹션 */}
                <div className="text-center mb-12">
                    <div className="inline-flex items-center gap-4 mb-6">
                        <div className="text-5xl sm:text-6xl animate-pulse">🩺</div>
                        <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold bg-gradient-to-r from-primary-600 to-primary-400 bg-clip-text text-transparent">
                            AI 영양제 추천 닥터
                        </h1>
                    </div>
                    <p className="text-lg sm:text-xl text-gray-600 mb-8 max-w-3xl mx-auto leading-relaxed">
                        증상을 자세히 말씀해주시면 AI가 당신의 건강 상태를 분석하여<br className="hidden sm:block" />
                        <span className="text-accent-600 font-semibold">개인 맞춤 영양제</span>를 추천해드립니다
                    </p>

                    {/* 검색 입력 */}
                    <div className="max-w-3xl mx-auto mb-8">
                        <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-medium border border-primary-100">
                            <div className="flex flex-col sm:flex-row gap-3">
                                <input
                                    type="text"
                                    className="flex-1 border-2 border-primary-200 p-4 rounded-xl shadow-sm focus:outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-500/20 text-lg transition-all duration-200"
                                    placeholder="예: 요즘 잠을 못 자고 피곤해요, 면역력이 떨어진 것 같아요..."
                                    value={query}
                                    onChange={(e) => setQuery(e.target.value)}
                                    onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                                />
                                <button
                                    onClick={() => handleSearch()}
                                    disabled={loading}
                                    className="bg-accent-500 hover:bg-accent-600 text-primary-900 px-6 sm:px-8 py-4 rounded-xl font-bold transition-all duration-300 transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none shadow-medium hover:shadow-strong"
                                >
                                    {loading ? (
                                        <span className="flex items-center gap-2">
                                            <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                                            분석 중...
                                        </span>
                                    ) : (
                                        <span className="flex items-center gap-2">
                                            추천받기
                                            <span>🚀</span>
                                        </span>
                                    )}
                                </button>
                            </div>
                        </div>
                    </div>

                {/* 예시 질문들 */}
                {!hasSearched && (
                    <div className="max-w-4xl mx-auto">
                        <p className="text-sm text-gray-500 mb-4">💡 이런 증상들을 클릭해보세요</p>
                        <div className="flex flex-wrap gap-3 justify-center">
                            {exampleQueries.map((example, index) => (
                                <button
                                    key={index}
                                    onClick={() => handleExampleClick(example)}
                                    className="bg-gray-100 hover:bg-primary-50 hover:text-primary-700 px-4 py-2 rounded-full text-sm transition border hover:border-primary-200"
                                >
                                    "{example}"
                                </button>
                            ))}
                        </div>
                    </div>
                )}
            </div>

                {/* 로딩 상태 */}
                {loading && (
                    <div className="text-center py-12">
                        <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mb-4"></div>
                        <p className="text-lg text-gray-600">AI가 증상을 분석하고 있습니다...</p>
                    </div>
                )}

                {/* 검색 결과 */}
                {!loading && results.length > 0 && (
                    <div className="max-w-7xl mx-auto">
                        <h2 className="text-2xl font-bold mb-6 text-center">
                            🎯 추천 결과 ({results.length}개)
                        </h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {results.map((item) => (
                                <ProductCard 
                                    key={item.id} 
                                    supplement={item.metadata} 
                                    showScore={item.score}
                                />
                            ))}
                        </div>
                    </div>
                )}

                {/* 검색 결과 없음 */}
                {!loading && hasSearched && results.length === 0 && (
                    <div className="text-center py-12">
                        <div className="text-6xl mb-4">😔</div>
                        <h3 className="text-xl font-bold text-gray-800 mb-2">추천 결과가 없습니다</h3>
                        <p className="text-gray-600 mb-6">다른 증상으로 다시 검색해보세요</p>
                        <button
                            onClick={() => {
                                setQuery('');
                                setHasSearched(false);
                                setResults([]);
                            }}
                            className="bg-accent-500 hover:bg-accent-600 text-primary-900 px-6 py-3 rounded-lg font-bold transition"
                        >
                            다시 검색하기
                        </button>
                    </div>
                )}

                {/* 초기 상태 */}
                {!loading && !hasSearched && (
                    <div className="text-center py-12">
                        <div className="text-6xl mb-4">🤖</div>
                        <p className="text-gray-500 text-lg">
                            증상을 입력하면 AI가 딱 맞는 영양제를 찾아드립니다
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AiRecommendPage;
