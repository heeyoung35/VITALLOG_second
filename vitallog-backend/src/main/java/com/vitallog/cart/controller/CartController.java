package com.vitallog.cart.controller;

import com.vitallog.cart.dto.CartResponseDTO;
import com.vitallog.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "유저 장바구니 관련 API 입니다.")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{cartNo}")
    @Operation(
            summary = "유저 장바구니 조회",
            description = "cartNo(장바구니 번호)를 이용해 해당 장바구니 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 장바구니 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 장바구니를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    // http://localhost:8080/cart/{cartNo}
    public ResponseEntity<CartResponseDTO> findCartByCartNo(
            @Parameter(description = "조회할 장바구니 번호", example = "3")
            @PathVariable Integer cartNo) {
        CartResponseDTO cartResponseDTO = cartService.findCartByCarNo(cartNo);

        return ResponseEntity.ok(cartResponseDTO);
    }

    // cartItem controller에서 생성
//    @PostMapping
//    public ResponseEntity<CartResponseDTO> registCart(@RequestBody CartRequestDTO cartRequestDTO) {
//        CartResponseDTO newCart = cartService.registCart(cartRequestDTO);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(newCart);
//    }


}
