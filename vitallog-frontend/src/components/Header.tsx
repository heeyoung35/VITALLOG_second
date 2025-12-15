// src/components/Header.tsx
import { Link, useNavigate } from 'react-router-dom';

const Header = () => {
    const navigate = useNavigate();
    const isLoggedIn = !!localStorage.getItem('accessToken');

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        alert('로그아웃 되었습니다.');
        navigate('/');
        window.location.reload(); // 상태 초기화
    };

    return (
        <header className="bg-white border-b shadow-sm sticky top-0 z-50">
            <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
                {/* 로고 */}
                <Link to="/" className="text-2xl font-bold text-green-600 flex items-center gap-2">
                    💊 VITALLOG
                </Link>

                {/* 네비게이션 */}
                <nav className="hidden md:flex gap-6 text-gray-600 font-medium">
                    <Link to="/" className="hover:text-green-600">홈</Link>
                    <Link to="/recommend" className="hover:text-green-600">AI 추천받기</Link>
                    {isLoggedIn && <Link to="/cart" className="hover:text-green-600">장바구니</Link>}
                </nav>

                {/* 우측 버튼 */}
                <div className="flex gap-3">
                    {isLoggedIn ? (
                        <button
                            onClick={handleLogout}
                            className="text-sm text-gray-500 hover:text-gray-800"
                        >
                            로그아웃
                        </button>
                    ) : (
                        <Link
                            to="/login"
                            className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-bold hover:bg-green-700 transition"
                        >
                            로그인
                        </Link>
                    )}
                </div>
            </div>
        </header>
    );
};

export default Header;