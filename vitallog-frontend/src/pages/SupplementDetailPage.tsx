// src/pages/SupplementDetailPage.tsx
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import { Supplement } from '../types';

const SupplementDetailPage = () => {
    const { id } = useParams(); // URL의 nutNo 가져오기
    const navigate = useNavigate();
    const [supplement, setSupplement] = useState<Supplement | null>(null);
    const [quantity, setQuantity] = useState(1);

    useEffect(() => {
        api.get(`/supplement/${id}`)
            .then(res => setSupplement(res.data))
            .catch(err => console.error(err));
    }, [id]);

    const handleAddToCart = async () => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert("로그인이 필요합니다.");
            navigate('/login');
            return;
        }

        try {
            // CartItemController에 맞게 요청 (API 명세 확인 필요, 일반적인 구조로 작성)
            // 예: POST /cart/items
            await api.post('/cart/items', {
                nutNo: supplement?.nutNo,
                quantity: quantity
            });

            if(window.confirm("장바구니에 담았습니다. 장바구니로 이동할까요?")) {
                navigate('/cart');
            }
        } catch (error) {
            alert("장바구니 담기에 실패했습니다.");
            console.error(error);
        }
    };

    if (!supplement) return <div className="p-10 text-center">불러오는 중...</div>;

    return (
        <div className="max-w-4xl mx-auto py-10 px-4">
            <div className="flex flex-col md:flex-row gap-8">
                {/* 이미지 영역 */}
                <div className="w-full md:w-1/2 bg-gray-100 rounded-xl h-80 flex items-center justify-center text-6xl">
                    💊
                </div>

                {/* 정보 영역 */}
                <div className="w-full md:w-1/2 flex flex-col justify-center">
                    <span className="text-green-600 font-bold mb-2">{supplement.primaryFnclty}</span>
                    <h1 className="text-3xl font-bold mb-4">{supplement.nutName}</h1>
                    <p className="text-gray-600 mb-6 border-b pb-6">
                        {supplement.rawName} 함유 | {supplement.shape} 형태 <br/>
                        {supplement.nutMthd}
                    </p>

                    <div className="text-2xl font-bold mb-6">
                        {supplement.price.toLocaleString()}원
                    </div>

                    <div className="flex gap-4 mb-4">
                        <div className="flex border rounded w-32">
                            <button onClick={() => setQuantity(Math.max(1, quantity - 1))} className="px-3 bg-gray-50">-</button>
                            <input type="text" readOnly value={quantity} className="w-full text-center outline-none" />
                            <button onClick={() => setQuantity(quantity + 1)} className="px-3 bg-gray-50">+</button>
                        </div>
                        <button
                            onClick={handleAddToCart}
                            className="flex-1 bg-green-600 text-white font-bold rounded hover:bg-green-700 py-3"
                        >
                            장바구니 담기
                        </button>
                    </div>

                    <div className="bg-red-50 text-red-600 p-3 rounded text-sm">
                        🚨 주의사항: {supplement.warning}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SupplementDetailPage;