package com.vitallog.cartitem.controller;

import com.vitallog.cartitem.dto.CartItemRequestDTO;
import com.vitallog.cartitem.dto.CartItemResponseDTO;
import com.vitallog.cartitem.dto.CartItemUpdateRequestDTO;
import com.vitallog.cartitem.repository.CartItemRepository;
import com.vitallog.cartitem.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CartItem", description = "장바구니 아이템 관련 API 입니다.")
@RestController
@RequestMapping("/cartItem")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;


    // 장바구니 상품 조회
    // http://localhost:8080/cartItem/{cartNo}
    @GetMapping("/{cartNo}")
    @Operation(summary = "장바구니 아이템 조회", description = "cartNo를 이용해서 해당 장바구니에 어떤 상품이 담겨 있는지 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장바구니 아이템 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 장바구니를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<List<CartItemResponseDTO>> findCartItemByCartNo(
            @Parameter(
                    description = "조회할 장바구니 번호",
                    example = "3"
            )
            @PathVariable Integer cartNo){

       List<CartItemResponseDTO> cartItems =
               cartItemService.findCartItemByCartNo(cartNo);

       return ResponseEntity.ok(cartItems);
    }

    // 장바구니 상품 담기
    // http://localhost:8080/cartItem?userNo={userNo} // 05e78837-609b-4c90-852b-dc34e2eba51c
    /*
    {
        "nutNo": {영양제 번호},
        "quantity": {수량}
    }
    * */
    @PostMapping
    @Operation(summary = "장바구니 상품 담기", description = "장바구니에 상품 담기/유저 장바구니가 없으면 생성 후 담기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장바구니 아이템 생성 완료"),
            @ApiResponse(responseCode = "200", description = "기존 장바구니 아이템 수량 증가"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<CartItemResponseDTO> registCartItem(
            @Parameter(description = "유저 번호", example = "f1340c7b-11fa-4d53-9b89-68bfdac9c1f1")
            @RequestParam String userNo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "장바구니에 담을 상품 정보"
            )
            @RequestBody CartItemRequestDTO cartItemRequestDTO){
        CartItemResponseDTO newCartItem = cartItemService.registCartItem(userNo, cartItemRequestDTO);

//        return ResponseEntity.status(HttpStatus.CREATED).body(newCartItem);
        return ResponseEntity.ok(newCartItem);
    }

    // 장바구니 상품 수정
    // http://localhost:8080/cartItem/{cartItemNo}
    /*
    {
        "quantity": {수량}
    }
    * */
    @PatchMapping("/{cartItemNo}")
    @Operation(summary = "장바구니 상품 수량 수정", description = "cartItemNo를 이용해 상품 수량 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장바구니 상품 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 장바구니 상품 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<CartItemResponseDTO> updateCartItemQuantity(
            @Parameter(description = "수정할 장바구니 아이템 번호", example = "5")
            @PathVariable Integer cartItemNo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 수량 정보"
            )
            @RequestBody CartItemUpdateRequestDTO quantity
    ){
        CartItemResponseDTO updatedItem = cartItemService.updateCartItemQuantity(cartItemNo, quantity);

        return ResponseEntity.ok(updatedItem);
    }

    // 장바구니 상품 삭제
    // http://localhost:8080/cartItem/{cartItemNo}
    @DeleteMapping("/{cartItemNo}")
    @Operation(summary = "장바구니 상품 삭제", description = "cartItemNo를 이용해서 장바구니에 담긴 상품 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장바구니 아이템 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 영양제를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public ResponseEntity<String> deleteCartItem(
            @Parameter(description = "삭제할 장바구니 아이템 번호", example = "7")
            @PathVariable int cartItemNo) {
        String daleteCartItem = cartItemService.deleteCartItem(cartItemNo);


        String message = cartItemNo + " / " + daleteCartItem + "이(가) 삭제되었습니다.";

        return ResponseEntity.ok(message);
    }
}
