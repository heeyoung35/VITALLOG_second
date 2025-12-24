// src/pages/LoginPage.tsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService'; // 리팩터링: 직접 API 호출 대신 Service 계층 사용
import { LoginRequest } from '../types';

const LoginPage = () => {
    // 리팩터링: 초기 상태값 정의
    const [formData, setFormData] = useState<LoginRequest>({ id: '', pw: '' });
    const [isSignUp, setIsSignUp] = useState(false);
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const navigate = useNavigate();

    // 리팩터링: 유효성 검사 로직 분리 (가독성 향상)
    const validateForm = () => {
        const newErrors: { [key: string]: string } = {};

        if (!formData.id.trim()) {
            newErrors.id = '아이디를 입력해주세요';
        } else if (formData.id.length < 3) {
            newErrors.id = '아이디는 3자 이상이어야 합니다';
        }

        if (!formData.pw.trim()) {
            newErrors.pw = '비밀번호를 입력해주세요';
        } else if (formData.pw.length < 4) {
            newErrors.pw = '비밀번호는 4자 이상이어야 합니다';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateForm()) return;

        setLoading(true);
        try {
            // 리팩터링: AuthService를 통한 비즈니스 로직 호출
            // UI 컴포넌트는 "데이터를 어떻게 가져오는지"가 아니라 "무엇을 할지"에 집중해야 합니다.
            if (isSignUp) {
                await AuthService.signUp(formData);
                alert('회원가입이 완료되었습니다! 로그인해주세요.');
                setIsSignUp(false);
                setFormData({ id: '', pw: '' });
            } else {
                await AuthService.login(formData);
                // 리팩터링: 토큰 저장은 Service 내부에서 처리하므로 UI에서는 제거
                alert('로그인 성공!');
                navigate('/');
            }
        } catch (error: any) {
            // 리팩터링: 에러 메시지 추출 로직 개선 (가능하다면 유틸리티로 분리 추천)
            const message = error.response?.data?.message ||
                (isSignUp ? '회원가입에 실패했습니다.' : '로그인에 실패했습니다.');
            alert(message);
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (field: keyof LoginRequest, value: string) => {
        setFormData(prev => ({ ...prev, [field]: value }));
        if (errors[field]) {
            setErrors(prev => ({ ...prev, [field]: '' }));
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-neutral-cream via-primary-50 to-primary-100 flex items-center justify-center py-8 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8 mx-auto">
                {/* 로고 섹션 */}
                <div className="text-center">
                    <Link to="/" className="inline-flex items-center justify-center gap-3 group">
                        <div className="text-4xl sm:text-5xl transform group-hover:scale-110 transition-transform duration-300">💊</div>
                        <span className="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-primary-600 to-primary-400 bg-clip-text text-transparent">
                            VITALLOG
                        </span>
                    </Link>
                    <h2 className="mt-6 text-2xl sm:text-3xl font-bold text-gray-900">
                        {isSignUp ? '새로운 시작' : '다시 만나서 반가워요'}
                    </h2>
                    <p className="mt-2 text-sm sm:text-base text-gray-600">
                        {isSignUp ? '건강한 라이프스타일의 첫 걸음을 시작하세요' : '건강 관리를 계속해보세요'}
                    </p>
                </div>

                {/* 폼 섹션 */}
                <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-medium p-6 sm:p-8 border border-primary-100">
                    <form className="space-y-6" onSubmit={handleSubmit}>
                        <div className="space-y-5">
                            <div>
                                <label htmlFor="id" className="block text-sm font-semibold text-gray-700 mb-2">
                                    아이디
                                </label>
                                <input
                                    id="id"
                                    type="text"
                                    required
                                    value={formData.id}
                                    onChange={(e) => handleInputChange('id', e.target.value)}
                                    className={`w-full px-4 py-3 border-2 rounded-xl shadow-sm transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500/20 ${errors.id
                                            ? 'border-red-300 focus:border-red-500'
                                            : 'border-gray-200 focus:border-primary-500 hover:border-primary-300'
                                        }`}
                                    placeholder="아이디를 입력하세요"
                                />
                                {errors.id && <p className="mt-2 text-sm text-red-600 flex items-center gap-1">
                                    <span>⚠️</span> {errors.id}
                                </p>}
                            </div>

                            <div>
                                <label htmlFor="pw" className="block text-sm font-semibold text-gray-700 mb-2">
                                    비밀번호
                                </label>
                                <input
                                    id="pw"
                                    type="password"
                                    required
                                    value={formData.pw}
                                    onChange={(e) => handleInputChange('pw', e.target.value)}
                                    className={`w-full px-4 py-3 border-2 rounded-xl shadow-sm transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500/20 ${errors.pw
                                            ? 'border-red-300 focus:border-red-500'
                                            : 'border-gray-200 focus:border-primary-500 hover:border-primary-300'
                                        }`}
                                    placeholder="비밀번호를 입력하세요"
                                />
                                {errors.pw && <p className="mt-2 text-sm text-red-600 flex items-center gap-1">
                                    <span>⚠️</span> {errors.pw}
                                </p>}
                            </div>
                        </div>

                        <div className="pt-2">
                            <button
                                type="submit"
                                disabled={loading}
                                className="w-full bg-accent-500 hover:bg-accent-600 text-primary-900 py-3 px-4 rounded-xl font-bold shadow-medium hover:shadow-strong transition-all duration-300 transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none focus:outline-none focus:ring-2 focus:ring-accent-500/50"
                            >
                                {loading ? (
                                    <span className="flex items-center justify-center gap-2">
                                        <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                                        처리 중...
                                    </span>
                                ) : (
                                    isSignUp ? '회원가입하기' : '로그인하기'
                                )}
                            </button>
                        </div>

                        <div className="text-center pt-4 border-t border-gray-100">
                            <button
                                type="button"
                                onClick={() => {
                                    setIsSignUp(!isSignUp);
                                    setFormData({ id: '', pw: '' });
                                    setErrors({});
                                }}
                                className="text-primary-600 hover:text-primary-700 text-sm font-medium transition-colors duration-200 hover:underline"
                            >
                                {isSignUp ? '이미 계정이 있으신가요? 로그인하기' : '계정이 없으신가요? 회원가입하기'}
                            </button>
                        </div>
                    </form>
                </div>

                {/* 추가 정보 */}
                <div className="text-center">
                    <p className="text-xs text-gray-500">
                        로그인하시면 <span className="text-accent-600 font-medium">개인 맞춤 영양제 추천</span>과<br />
                        <span className="text-accent-600 font-medium">건강 관리 서비스</span>를 이용하실 수 있습니다.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;