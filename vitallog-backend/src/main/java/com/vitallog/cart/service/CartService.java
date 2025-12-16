package com.vitallog.cart.service;


import com.vitallog.cart.Entity.Cart;
import com.vitallog.cart.dto.CartResponseDTO;
import com.vitallog.cart.repository.CartRepository;
import com.vitallog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;


    // 사용자 장바구니 조회
    public CartResponseDTO findCartByCarNo(Integer carNo) {
        Cart foundCart = cartRepository.findById(carNo).orElseThrow(
                () -> new IllegalArgumentException("장바구니가 없습니다.")
        );
        return modelMapper.map(foundCart, CartResponseDTO.class);
    }

    // cartItem service에서 생성
//    // 사용자 장바구니 생성
//    public CartResponseDTO registCart(CartRequestDTO cartRequestDTO) {
//
//        User foundUser = userRepository.findById(cartRequestDTO.getUserNo())
//                .orElseThrow(() -> new IllegalArgumentException("헤당 유저가 존재하지 않습니다."));
//
//        if(cartRepository.findByUser_UserNo(foundUser.getUserNo()).isPresent()) {
//            throw new IllegalArgumentException("이미 장바구니가 생성된 회원입니다.");
//        }
//
//        Cart newCart = Cart.builder()
//                .user(foundUser)
////                .createdAt(cartRequestDTO.getCreatedAt())
//                .build();
//        try{
//            Cart savedCart = cartRepository.save(newCart);
//            return modelMapper.map(savedCart, CartResponseDTO.class);
//        }catch (DataIntegrityViolationException e) {
//            throw new IllegalArgumentException("이미 장바구니가 생성된 회원입니다.");
//        }
//
//    }

}
