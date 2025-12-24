import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';

const Header = () => {
    const navigate = useNavigate();
    const isLoggedIn = !!localStorage.getItem('accessToken');
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        alert('로그아웃 되었습니다.');
        navigate('/');
        window.location.reload(); // 상태 초기화
    };

    return (
        // 리팩터링: 요청받은 헤더 배경색 #4682A9 적용
        <header className="bg-[#4682A9] shadow-lg sticky top-0 z-50">
            <div className="w-full max-w-none px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16 lg:h-18">
                    {/* 로고 */}
                    <Link to="/" className="flex items-center gap-2 group flex-shrink-0">
                        <div className="text-2xl lg:text-3xl">💊</div>
                        <span className="text-xl lg:text-2xl font-bold text-white group-hover:text-accent-500 transition-all duration-300">
                            VITALLOG
                        </span>
                    </Link>

                    {/* 데스크톱 네비게이션 */}
                    <nav className="hidden md:flex items-center gap-6 lg:gap-8">
                        <Link
                            to="/"
                            className="text-white/90 hover:text-accent-500 font-medium transition-colors duration-200 relative group"
                        >
                            홈
                            <span className="absolute -bottom-1 left-0 w-0 h-0.5 bg-accent-500 group-hover:w-full transition-all duration-300"></span>
                        </Link>
                        <Link
                            to="/recommend"
                            className="text-white/90 hover:text-accent-500 font-medium transition-colors duration-200 relative group"
                        >
                            AI 추천받기
                            <span className="absolute -bottom-1 left-0 w-0 h-0.5 bg-accent-500 group-hover:w-full transition-all duration-300"></span>
                        </Link>
                        {isLoggedIn && (
                            <Link
                                to="/cart"
                                className="text-white/90 hover:text-accent-500 font-medium transition-colors duration-200 relative group"
                            >
                                장바구니
                                <span className="absolute -bottom-1 left-0 w-0 h-0.5 bg-accent-500 group-hover:w-full transition-all duration-300"></span>
                            </Link>
                        )}
                    </nav>

                    {/* 우측 버튼 */}
                    <div className="flex items-center gap-3 flex-shrink-0">
                        {isLoggedIn ? (
                            <button
                                onClick={handleLogout}
                                className="text-sm text-white/80 hover:text-accent-500 font-medium transition-colors duration-200 hidden md:block"
                            >
                                로그아웃
                            </button>
                        ) : (
                            <Link
                                to="/login"
                                className="bg-accent-500 hover:bg-accent-600 text-primary-900 px-4 py-2 lg:px-6 lg:py-2.5 rounded-xl text-sm lg:text-base font-bold shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-0.5 hidden md:block"
                            >
                                로그인
                            </Link>
                        )}

                        {/* 모바일 메뉴 버튼 */}
                        <button
                            className="md:hidden p-2 text-white/80 hover:text-accent-500 transition-colors"
                            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                {isMobileMenuOpen ? (
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                ) : (
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                                )}
                            </svg>
                        </button>
                    </div>
                </div>

                {/* 모바일 메뉴 드롭다운 */}
                {isMobileMenuOpen && (
                    <div className="md:hidden py-4 border-t border-white/10 animate-fadeIn">
                        <div className="flex flex-col gap-4">
                            <Link
                                to="/"
                                className="text-white hover:text-accent-500 font-medium px-2 py-1"
                                onClick={() => setIsMobileMenuOpen(false)}
                            >
                                홈
                            </Link>
                            <Link
                                to="/recommend"
                                className="text-white hover:text-accent-500 font-medium px-2 py-1"
                                onClick={() => setIsMobileMenuOpen(false)}
                            >
                                AI 추천받기
                            </Link>
                            {isLoggedIn && (
                                <Link
                                    to="/cart"
                                    className="text-white hover:text-accent-500 font-medium px-2 py-1"
                                    onClick={() => setIsMobileMenuOpen(false)}
                                >
                                    장바구니
                                </Link>
                            )}
                            <div className="pt-2 border-t border-white/10">
                                {isLoggedIn ? (
                                    <button
                                        onClick={handleLogout}
                                        className="w-full text-left text-white/80 hover:text-accent-500 font-medium px-2 py-1"
                                    >
                                        로그아웃
                                    </button>
                                ) : (
                                    <Link
                                        to="/login"
                                        className="block text-white/80 hover:text-accent-500 font-medium px-2 py-1"
                                        onClick={() => setIsMobileMenuOpen(false)}
                                    >
                                        로그인
                                    </Link>
                                )}
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </header>
    );
};

export default Header;