// src/services/CartService.ts
import api from '../api/axiosConfig';
import { CartItem } from '../types';

class CartService {
    private static instance: CartService;
    
    public static getInstance(): CartService {
        if (!CartService.instance) {
            CartService.instance = new CartService();
        }
        return CartService.instance;
    }

    async getCartItems(): Promise<CartItem[]> {
        const cartNo = localStorage.getItem('cartNo') || '1';
        const response = await api.get(`/cartItem/${cartNo}`);
        return response.data;
    }

    async addToCart(nutNo: number, quantity: number = 1): Promise<void> {
        const userNo = localStorage.getItem('userNo') || 'temp-user-id';
        await api.post('/cartItem', { nutNo, quantity }, {
            params: { userNo }
        });
    }

    async updateCartItem(cartItemNo: number, quantity: number): Promise<void> {
        await api.patch(`/cartItem/${cartItemNo}`, { quantity });
    }

    async removeFromCart(cartItemNo: number): Promise<void> {
        await api.delete(`/cartItem/${cartItemNo}`);
    }

    async clearCart(): Promise<void> {
        // 전체 장바구니 삭제는 백엔드에 해당 API가 없으므로 개별 삭제로 구현
        const items = await this.getCartItems();
        for (const item of items) {
            await this.removeFromCart(item.cartItemNo);
        }
    }

    async getCartCount(): Promise<number> {
        try {
            const items = await this.getCartItems();
            return items.reduce((total, item) => total + item.quantity, 0);
        } catch (error) {
            return 0;
        }
    }

    // 로컬 스토리지를 이용한 임시 장바구니 (로그인하지 않은 사용자용)
    getLocalCartItems(): CartItem[] {
        const items = localStorage.getItem('localCart');
        return items ? JSON.parse(items) : [];
    }

    addToLocalCart(nutNo: number, nutName: string, price: number, quantity: number = 1): void {
        const items = this.getLocalCartItems();
        const existingItem = items.find(item => item.nutNo === nutNo);
        
        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            items.push({
                cartItemNo: Date.now(), // 임시 ID
                nutNo,
                nutName,
                price,
                quantity
            });
        }
        
        localStorage.setItem('localCart', JSON.stringify(items));
    }

    removeFromLocalCart(nutNo: number): void {
        const items = this.getLocalCartItems().filter(item => item.nutNo !== nutNo);
        localStorage.setItem('localCart', JSON.stringify(items));
    }

    clearLocalCart(): void {
        localStorage.removeItem('localCart');
    }

    async syncLocalCartToServer(): Promise<void> {
        const localItems = this.getLocalCartItems();
        if (localItems.length === 0) return;

        try {
            for (const item of localItems) {
                await this.addToCart(item.nutNo, item.quantity);
            }
            this.clearLocalCart();
        } catch (error) {
            console.error('로컬 장바구니 동기화 실패:', error);
        }
    }
}

export default CartService.getInstance();