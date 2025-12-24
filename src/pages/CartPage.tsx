// src/pages/CartPage.tsx
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import { CartItem } from '../types';

const CartPage = () => {
    const [cartItems, setCartItems] = useState<CartItem[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert('로그인이 필요합니다.');
            navigate('/login');
            return;
        }
        fetchCartItems();
    }, [navigate]);

    const fetchCartItems = async () => {
        try {
            // 사용자의 장바구니 번호를 가져와야 함 (임시로 1번 사용)
            const cartNo = localStorage.getItem('cartNo') || '1';
            const response = await api.get(`/cartItem/${cartNo}`);
            setCartItems(response.data);
        } catch (error) {
            console.error('장바구니 조회 실패', error);
            alert('장바구니를 불러오지 못했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const updateQuantity = async (cartItemNo: number, newQuantity: number) => {
        if (newQuantity < 1) return;
        
        try {
            await api.patch(`/cartItem/${cartItemNo}`, { quantity: newQuantity });
            setCartItems(prev => 
                prev.map(item => 
                    item.cartItemNo === cartItemNo 
                        ? { ...item, quantity: newQuantity }
                        : item
                )
            );
        } catch (error) {
            console.error('수량 변경 실패', error);
            alert('수량 변경에 실패했습니다.');
        }
    };

    const removeItem = async (cartItemNo: number) => {
        if (!window.confirm('이 상품을 장바구니에서 제거하시겠습니까?')) return;
        
        try {
            await api.delete(`/cartItem/${cartItemNo}`);
            setCartItems(prev => prev.filter(item => item.cartItemNo !== cartItemNo));
            alert('상품이 제거되었습니다.');
        } catch (error) {
            console.error('상품 제거 실패', error);
            alert('상품 제거에 실패했습니다.');
        }
    };

    const getTotalPrice = () => {
        return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
    };

    const handleCheckout = () => {
        if (cartItems.length === 0) {
            alert('장바구니가 비어있습니다.');
            return;
        }
        
        // 실제 결제 시스템 연동 시 구현
        alert('결제 기능은 준비 중입니다.');
    };

    if (loading) {
        return (
            <div className="max-w-4xl mx-auto py-8 px-4">
                <div className="text-center py-12">
                    <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mb-4"></div>
                    <p className="text-lg text-gray-600">장바구니를 불러오는 중...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto py-8 px-4">
            <div className="flex items-center justify-between mb-8">
                <h1 className="text-3xl font-bold text-gray-800">🛒 장바구니</h1>
                <Link 
                    to="/" 
                    className="text-green-600 hover:text-green-700 font-medium"
                >
                    ← 쇼핑 계속하기
                </Link>
            </div>

            {cartItems.length === 0 ? (
                <div className="text-center py-16">
                    <div className="text-6xl mb-4">🛒</div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">장바구니가 비어있습니다</h2>
                    <p className="text-gray-600 mb-8">원하는 영양제를 담아보세요!</p>
                    <div className="space-y-4">
                        <Link 
                            to="/" 
                            className="inline-block bg-green-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-green-700 transition"
                        >
                            상품 둘러보기
                        </Link>
                        <br />
                        <Link 
                            to="/recommend" 
                            className="inline-block bg-blue-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-blue-700 transition"
                        >
                            AI 추천받기
                        </Link>
                    </div>
                </div>
            ) : (
                <div className="space-y-6">
                    {/* 장바구니 아이템들 */}
                    <div className="bg-white rounded-xl shadow-sm border">
                        {cartItems.map((item, index) => (
                            <div key={item.cartItemNo} className={`p-6 ${index !== cartItems.length - 1 ? 'border-b' : ''}`}>
                                <div className="flex items-center gap-4">
                                    {/* 상품 이미지 */}
                                    <div className="w-20 h-20 bg-gray-100 rounded-lg flex items-center justify-center text-2xl">
                                        💊
                                    </div>
                                    
                                    {/* 상품 정보 */}
                                    <div className="flex-1">
                                        <Link 
                                            to={`/supplement/${item.nutNo}`}
                                            className="text-lg font-bold text-gray-800 hover:text-green-600 transition"
                                        >
                                            {item.nutName}
                                        </Link>
                                        <p className="text-gray-600 text-sm mt-1">
                                            개당 {item.price.toLocaleString()}원
                                        </p>
                                    </div>
                                    
                                    {/* 수량 조절 */}
                                    <div className="flex items-center gap-3">
                                        <div className="flex border rounded-lg">
                                            <button 
                                                onClick={() => updateQuantity(item.cartItemNo, item.quantity - 1)}
                                                className="px-3 py-2 hover:bg-gray-50 text-gray-600"
                                                disabled={item.quantity <= 1}
                                            >
                                                -
                                            </button>
                                            <span className="px-4 py-2 border-x text-center min-w-[60px]">
                                                {item.quantity}
                                            </span>
                                            <button 
                                                onClick={() => updateQuantity(item.cartItemNo, item.quantity + 1)}
                                                className="px-3 py-2 hover:bg-gray-50 text-gray-600"
                                            >
                                                +
                                            </button>
                                        </div>
                                        
                                        {/* 가격 */}
                                        <div className="text-right min-w-[100px]">
                                            <p className="font-bold text-lg">
                                                {(item.price * item.quantity).toLocaleString()}원
                                            </p>
                                        </div>
                                        
                                        {/* 삭제 버튼 */}
                                        <button 
                                            onClick={() => removeItem(item.cartItemNo)}
                                            className="text-red-500 hover:text-red-700 p-2"
                                            title="상품 제거"
                                        >
                                            🗑️
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* 주문 요약 */}
                    <div className="bg-gray-50 rounded-xl p-6">
                        <div className="flex justify-between items-center mb-4">
                            <span className="text-lg font-medium">총 상품 개수</span>
                            <span className="text-lg">{cartItems.reduce((sum, item) => sum + item.quantity, 0)}개</span>
                        </div>
                        <div className="flex justify-between items-center mb-4">
                            <span className="text-lg font-medium">상품 금액</span>
                            <span className="text-lg">{getTotalPrice().toLocaleString()}원</span>
                        </div>
                        <div className="flex justify-between items-center mb-4">
                            <span className="text-lg font-medium">배송비</span>
                            <span className="text-lg">무료</span>
                        </div>
                        <hr className="my-4" />
                        <div className="flex justify-between items-center mb-6">
                            <span className="text-xl font-bold">총 결제 금액</span>
                            <span className="text-2xl font-bold text-green-600">
                                {getTotalPrice().toLocaleString()}원
                            </span>
                        </div>
                        
                        <button 
                            onClick={handleCheckout}
                            className="w-full bg-green-600 text-white py-4 rounded-lg text-lg font-bold hover:bg-green-700 transition"
                        >
                            주문하기
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CartPage;