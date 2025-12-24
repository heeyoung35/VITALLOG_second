// src/utils/format.ts

export const formatPrice = (price: number): string => {
    return price.toLocaleString('ko-KR') + '원';
};

export const formatNumber = (num: number): string => {
    return num.toLocaleString('ko-KR');
};

export const formatDate = (date: string | Date): string => {
    const d = new Date(date);
    return d.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
};

export const formatDateTime = (date: string | Date): string => {
    const d = new Date(date);
    return d.toLocaleString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

export const truncateText = (text: string, maxLength: number): string => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
};

export const formatScore = (score: number): string => {
    return `${(score * 100).toFixed(0)}%`;
};

export const formatStock = (stock: number): string => {
    if (stock === 0) return '품절';
    if (stock < 10) return `재고 부족 (${stock}개)`;
    return `재고 ${stock}개`;
};

export const formatCategory = (category: string): string => {
    // 카테고리 문자열을 사용자 친화적으로 변환
    const categoryMap: { [key: string]: string } = {
        'vitamin': '비타민',
        'mineral': '미네랄',
        'protein': '단백질',
        'omega': '오메가',
        'probiotics': '유산균',
        'collagen': '콜라겐',
        'joint': '관절',
        'immune': '면역',
        'energy': '에너지',
        'sleep': '수면',
        'diet': '다이어트',
        'beauty': '뷰티'
    };
    
    return categoryMap[category.toLowerCase()] || category;
};