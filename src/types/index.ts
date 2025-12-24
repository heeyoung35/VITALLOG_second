// src/types/index.ts

// 로그인 요청
export interface LoginRequest {
    id: string;
    pw: string;
}

// 로그인 응답
export interface LoginResponse {
    token: string;
    user?: {
        userNo: string;
        userId: string;
        userName?: string;
    };
}

// 영양제 데이터 (백엔드 SupplementResponseDTO와 일치시킴)
export interface Supplement {
    nutNo: number;        // id -> nutNo 변경
    nutName: string;      // name -> nutName 변경
    price: number;
    nutMthd: string;      // 섭취방법
    primaryFnclty: string;// 효능
    rawName: string;      // 성분
    shape: string;        // 형태
    warning: string;      // 주의사항
    stock: number;
    // 이미지가 없다면 포트폴리오용 더미 이미지 사용 예정
}

// AI 추천 결과
export interface AiRecommendation {
    id: string;
    score: number;
    metadata: Supplement;
}

// 장바구니 아이템
export interface CartItem {
    cartItemNo: number;
    nutNo: number;
    nutName: string;
    price: number;
    quantity: number;
}

// 사용자 정보
export interface User {
    userNo: number;
    id: string;
    name?: string;
    email?: string;
}

// API 응답 래퍼
export interface ApiResponse<T> {
    success: boolean;
    data: T;
    message?: string;
    error?: string;
}

// 페이지네이션 응답
export interface PaginatedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
}

// 토스트 메시지
export interface ToastMessage {
    id: string;
    message: string;
    type: 'success' | 'error' | 'info';
    duration?: number;
}