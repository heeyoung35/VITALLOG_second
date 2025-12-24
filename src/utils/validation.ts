// src/utils/validation.ts

export const validateId = (id: string): string | null => {
    if (!id.trim()) {
        return '아이디를 입력해주세요';
    }
    if (id.length < 3) {
        return '아이디는 3자 이상이어야 합니다';
    }
    if (id.length > 20) {
        return '아이디는 20자 이하여야 합니다';
    }
    if (!/^[a-zA-Z0-9_]+$/.test(id)) {
        return '아이디는 영문, 숫자, 언더스코어만 사용 가능합니다';
    }
    return null;
};

export const validatePassword = (password: string): string | null => {
    if (!password.trim()) {
        return '비밀번호를 입력해주세요';
    }
    if (password.length < 4) {
        return '비밀번호는 4자 이상이어야 합니다';
    }
    if (password.length > 50) {
        return '비밀번호는 50자 이하여야 합니다';
    }
    return null;
};

export const validateEmail = (email: string): string | null => {
    if (!email.trim()) {
        return '이메일을 입력해주세요';
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        return '올바른 이메일 형식이 아닙니다';
    }
    return null;
};

export const validateQuantity = (quantity: number): string | null => {
    if (quantity < 1) {
        return '수량은 1개 이상이어야 합니다';
    }
    if (quantity > 999) {
        return '수량은 999개 이하여야 합니다';
    }
    return null;
};

export const validateSearchQuery = (query: string): string | null => {
    if (!query.trim()) {
        return '검색어를 입력해주세요';
    }
    if (query.length < 2) {
        return '검색어는 2자 이상 입력해주세요';
    }
    if (query.length > 100) {
        return '검색어는 100자 이하로 입력해주세요';
    }
    return null;
};