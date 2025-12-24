// src/components/ApiTest.tsx
import { useState } from 'react';
import api from '../api/axiosConfig';

const ApiTest = () => {
    const [testResult, setTestResult] = useState<string>('');
    const [loading, setLoading] = useState(false);

    const testConnection = async () => {
        setLoading(true);
        try {
            const response = await api.get('/supplement?page=0&size=5');
            setTestResult(`✅ 연결 성공! ${response.data.content?.length || 0}개 상품 조회됨`);
        } catch (error: any) {
            setTestResult(`❌ 연결 실패: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    const testLogin = async () => {
        setLoading(true);
        try {
            const response = await api.post('/api/user/login', {
                userId: 'test',
                pwd: 'test'
            });
            setTestResult(`✅ 로그인 테스트: ${response.status === 200 ? '성공' : '실패'}`);
        } catch (error: any) {
            setTestResult(`❌ 로그인 테스트 실패: ${error.response?.data?.message || error.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed bottom-4 right-4 bg-white border rounded-lg p-4 shadow-lg z-50">
            <h3 className="font-bold mb-2">🔧 API 연결 테스트</h3>
            <div className="space-y-2">
                <button 
                    onClick={testConnection}
                    disabled={loading}
                    className="block w-full bg-blue-500 text-white px-3 py-1 rounded text-sm hover:bg-blue-600 disabled:opacity-50"
                >
                    {loading ? '테스트 중...' : '상품 조회 테스트'}
                </button>
                <button 
                    onClick={testLogin}
                    disabled={loading}
                    className="block w-full bg-green-500 text-white px-3 py-1 rounded text-sm hover:bg-green-600 disabled:opacity-50"
                >
                    {loading ? '테스트 중...' : '로그인 테스트'}
                </button>
            </div>
            {testResult && (
                <div className="mt-2 p-2 bg-gray-100 rounded text-xs">
                    {testResult}
                </div>
            )}
        </div>
    );
};

export default ApiTest;