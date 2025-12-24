// src/utils/storage.ts

export class StorageUtils {
    // 로컬 스토리지 관련
    static setItem(key: string, value: any): void {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('로컬 스토리지 저장 실패:', error);
        }
    }

    static getItem<T>(key: string): T | null {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (error) {
            console.error('로컬 스토리지 조회 실패:', error);
            return null;
        }
    }

    static removeItem(key: string): void {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error('로컬 스토리지 삭제 실패:', error);
        }
    }

    static clear(): void {
        try {
            localStorage.clear();
        } catch (error) {
            console.error('로컬 스토리지 초기화 실패:', error);
        }
    }

    // 세션 스토리지 관련
    static setSessionItem(key: string, value: any): void {
        try {
            sessionStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('세션 스토리지 저장 실패:', error);
        }
    }

    static getSessionItem<T>(key: string): T | null {
        try {
            const item = sessionStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (error) {
            console.error('세션 스토리지 조회 실패:', error);
            return null;
        }
    }

    static removeSessionItem(key: string): void {
        try {
            sessionStorage.removeItem(key);
        } catch (error) {
            console.error('세션 스토리지 삭제 실패:', error);
        }
    }

    // 토큰 관련 유틸리티
    static getAccessToken(): string | null {
        return this.getItem<string>('accessToken');
    }

    static setAccessToken(token: string): void {
        this.setItem('accessToken', token);
    }

    static getRefreshToken(): string | null {
        return this.getItem<string>('refreshToken');
    }

    static setRefreshToken(token: string): void {
        this.setItem('refreshToken', token);
    }

    static clearTokens(): void {
        this.removeItem('accessToken');
        this.removeItem('refreshToken');
    }

    // 사용자 정보 관련
    static getUserInfo(): any {
        return this.getItem('userInfo');
    }

    static setUserInfo(userInfo: any): void {
        this.setItem('userInfo', userInfo);
    }

    static clearUserInfo(): void {
        this.removeItem('userInfo');
    }
}