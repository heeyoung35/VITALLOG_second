package com.vitallog.cartitem.service;


import com.vitallog.cart.Entity.Cart;
import com.vitallog.cart.repository.CartRepository;
import com.vitallog.cartitem.dto.CartItemRequestDTO;
import com.vitallog.cartitem.dto.CartItemResponseDTO;
import com.vitallog.cartitem.dto.CartItemUpdateRequestDTO;
import com.vitallog.cartitem.entity.CartItem;
import com.vitallog.cartitem.repository.CartItemRepository;
import com.vitallog.supplement.entity.Supplement;
import com.vitallog.supplement.repository.SupplementRepository;
import com.vitallog.user.entity.User;
import com.vitallog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final SupplementRepository supplementRepository;
    private final UserRepository userRepository;

    // 장바구니 상품 조회
    public List<CartItemResponseDTO> findCartItemByCartNo(Integer cartNo) {
        Cart foundCart = cartRepository.findById(cartNo).orElseThrow(
                () -> new IllegalArgumentException("장바구니가 없습니다."));

        List<CartItem> cartItemList = cartItemRepository.findByCart_CartNo(cartNo);

        return cartItemList.stream()
                .map(this::convertToResponseDTO)  // 또는 ModelMapper 사용
                .collect(Collectors.toList());
    }

    private CartItemResponseDTO convertToResponseDTO(CartItem cartItem) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartItemNo(cartItem.getCartItemNo());
        dto.setCartNo(cartItem.getCart().getCartNo());
        dto.setNutNo(cartItem.getSupplement().getNutNo());
        dto.setNutName(cartItem.getSupplement().getNutName());
        dto.setPrice(cartItem.getSupplement().getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setCreatedAt(cartItem.getCreatedAt());
        return dto;
    }

    // 장바구니 상품 등록
    public CartItemResponseDTO registCartItem(String userNo, CartItemRequestDTO cartItemRequestDTO) {

        // 유저 조회
        User user = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 존재하지 않습니다."));


        // 새로운 카트 조회 or 생성
        Cart cart = cartRepository.findByUser_UserNo(userNo)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });

        // 상품 조회
        Supplement foundSup = supplementRepository.findById(cartItemRequestDTO.getNutNo())
                .orElseThrow(() -> new IllegalArgumentException("없는 상품입니다."));

        int cartQty = cartItemRequestDTO.getQuantity();
        int stock = foundSup.getStock();

        // 재고 체크
        if(stock <= 0) {
            throw new IllegalArgumentException("해당 상품은 재고가 없어 담을 수 없습니다.");
        }
        if (cartQty > stock) {
            throw new IllegalArgumentException("요청하신 수향(" + cartQty + ")이 재고(" + stock + ") 보다 많습니다.");
        }

        // 장바구니 안에 해당 상품이 있는지 검사
        Optional<CartItem> existingItemOpt =
                cartItemRepository.findByCart_CartNoAndSupplement_NutNo(cart.getCartNo(), foundSup.getNutNo());

        // 기존 상품 존재 -> 수량 증가
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQty = existingItem.getQuantity() + cartQty;

            // 재고보다 많은지 다시 검사
            if (newQty > stock)
                throw new IllegalArgumentException("요청하신 수향(" + newQty + ")이 재고(" + stock + ") 보다 많습니다.");

            existingItem.setQuantity(newQty);
            CartItem updatedItem = cartItemRepository.save(existingItem);

            return convertToResponseDTO(updatedItem);
        }

        // 신규 CartItem 생성
        CartItem newCartItem = CartItem.builder()
                .cart(cart)
                .supplement(foundSup)
//                .createdAt(cartItemRequestDTO.getCreatedAt())
                .quantity(cartQty)
                .build();

        CartItem savedCartItem = cartItemRepository.save(newCartItem);

        CartItemResponseDTO responseDTO = new CartItemResponseDTO();
        responseDTO.setCartNo(savedCartItem.getCart().getCartNo());
        responseDTO.setCartItemNo(savedCartItem.getCartItemNo());
        responseDTO.setQuantity(savedCartItem.getQuantity());
        responseDTO.setPrice(savedCartItem.getSupplement().getPrice());
        responseDTO.setNutNo(savedCartItem.getSupplement().getNutNo());
        responseDTO.setCreatedAt(savedCartItem.getCreatedAt());
        responseDTO.setNutName(savedCartItem.getSupplement().getNutName());
        return responseDTO;

    }

    // 장바구니 상품 수정
    public CartItemResponseDTO updateCartItemQuantity(Integer CartItemNo, CartItemUpdateRequestDTO requestDTO) {

        CartItem foundCartItem = cartItemRepository.findById(CartItemNo)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 없는 상품입니다."));

        if (requestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        foundCartItem.setQuantity(requestDTO.getQuantity());

        CartItem updatadCartItem = cartItemRepository.save(foundCartItem);

        return convertToResponseDTO(updatadCartItem);

    }

    // 장바구니 상품 삭제
    public String  deleteCartItem(int cartItemNo) {

        CartItem cartItem = cartItemRepository.findById(cartItemNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 되는 장바구니 상품이 없습니다."));

        String deleteCart = cartItem.getCartItemNo().toString();

        cartItemRepository.delete(cartItem);

        return deleteCart;
    }
}
