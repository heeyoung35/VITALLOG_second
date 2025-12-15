// src/pages/LoginPage.tsx
import React, { useState } from 'react';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
    const [id, setId] = useState('');
    const [pw, setPw] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            // 백엔드 AuthController 경로 확인 필요 (/auth/sign-in 등으로 가정)
            const response = await api.post('/sign-in', { id, pw });

            // LoginResponse 구조에 맞춰 토큰 저장
            const { accessToken } = response.data;
            localStorage.setItem('accessToken', accessToken);

            alert('로그인 성공!');
            navigate('/');
        } catch (error) {
            console.error(error);
            alert('로그인 실패. 아이디와 비밀번호를 확인하세요.');
        }
    };

    return (
        <div className="max-w-md mx-auto mt-10 border p-6 rounded">
            <h2 className="text-2xl font-bold mb-4">로그인</h2>
            <form onSubmit={handleLogin} className="flex flex-col gap-4">
                <input
                    type="text"
                    placeholder="아이디"
                    value={id}
                    onChange={(e) => setId(e.target.value)}
                    className="border p-2 rounded"
                />
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={pw}
                    onChange={(e) => setPw(e.target.value)}
                    className="border p-2 rounded"
                />
                <button type="submit" className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
                    로그인
                </button>
            </form>
        </div>
    );
};

export default LoginPage;