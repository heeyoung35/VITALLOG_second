// src/components/ProductCard.tsx
import { Link } from 'react-router-dom';
import { type Supplement } from '../types';

interface ProductCardProps {
    supplement: Supplement;
    showScore?: number;
}

const ProductCard = ({ supplement, showScore }: ProductCardProps) => {
    return (
        <Link to={`/supplement/${supplement.nutNo}`} className="group block h-full">
            <div className="flex flex-col h-full">
                {/* 상품 이미지 영역 */}
                <div className="relative aspect-square overflow-hidden rounded-xl bg-gray-100 mb-3 hover:shadow-lg transition-shadow duration-300">
                    <img
                        src="/sample_product.jpg"
                        alt={supplement.nutName}
                        className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                    />

                    {/* 찜하기(하트) 버튼 - 상단 우측 */}
                    <button
                        className="absolute top-3 right-3 p-2 rounded-full hover:bg-white/10 active:scale-90 transition-transform z-10"
                        onClick={(e) => {
                            e.preventDefault(); // 링크 이동 방지
                            // 찜하기 로직 추가 예정
                        }}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="rgba(0,0,0,0.5)"
                            stroke="white"
                            strokeWidth="2"
                            className="w-6 h-6 hover:fill-red-500 hover:stroke-red-500 transition-colors"
                        >
                            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                        </svg>
                    </button>

                    {/* 추천 점수 뱃지 (옵션) - 좌측 상단 */}
                    {showScore && (
                        <div className="absolute top-3 left-3 bg-white/90 backdrop-blur-sm px-2 py-1 rounded-md text-xs font-bold shadow-sm">
                            {(showScore * 100).toFixed(0)}% 일치
                        </div>
                    )}
                </div>

                {/* 상품 정보 영역 (텍스트 위주) */}
                <div className="flex flex-col gap-1">
                    <div className="flex justify-between items-start">
                        <h3 className="font-semibold text-gray-900 group-hover:text-primary-600 transition-colors line-clamp-1">
                            {supplement.nutName}
                        </h3>
                        <div className="flex items-center gap-1 shrink-0">
                            <span className="text-sm">★</span>
                            <span className="text-sm text-gray-700">4.8</span>
                        </div>
                    </div>

                    <p className="text-gray-500 text-sm line-clamp-1">
                        {supplement.primaryFnclty}
                    </p>

                    <div className="mt-1 flex items-baseline gap-1">
                        <span className="font-semibold text-gray-900">
                            ￦{supplement.price.toLocaleString()}
                        </span>
                        <span className="text-gray-500 text-sm font-normal">
                            / 개
                        </span>
                    </div>
                </div>
            </div>
        </Link>
    );
};

export default ProductCard;