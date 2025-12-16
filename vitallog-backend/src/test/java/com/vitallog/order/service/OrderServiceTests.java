//package com.vitallog.order.service;
//
//import com.vitallog.order.Repository.OrderRepository;
//import com.vitallog.order.dto.OrderItemDTO;
//import com.vitallog.order.dto.OrderCreateRequestDTO;
//import com.vitallog.order.entity.Order;
//import com.vitallog.supplement.entity.Supplement;
//import com.vitallog.supplement.repository.SupplementRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat; // 이 줄로 변경합니다.
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//// JUnit 5와 Mockito 사용
//@ExtendWith(MockitoExtension.class)
//public class OrderServiceTests {
//
//    @InjectMocks
//    private OrderService orderService; // 테스트 대상 서비스
//
//    @Mock
//    private SupplementRepository supplementRepository; // Mock 객체
//    @Mock
//    private OrderRepository orderRepository; // Mock 객체
//
//    // 더미 데이터 (위에서 정의된 DummyData 클래스를 사용한다고 가정)
//    private final UUID TEST_USER_UUID = DummyData.USER_UUID;
//    private final int TEST_NUT_NO_1 = DummyData.NUT_NO_1;
//    private final int TEST_NUT_NO_2 = DummyData.NUT_NO_2;
//
//    @Test
//    @DisplayName("성공: 주문 생성 및 재고 감소, 총 가격 계산이 정상적으로 이루어져야 한다")
//    void createOrder_Success() {
//        // 1. Given (준비)
//        // 상품 1: 가격 10000원, 재고 50개, 주문 수량 2개
//        Supplement supplement1 = DummyData.createSupplement(TEST_NUT_NO_1, 10000, 50);
//        OrderItemDTO itemDto1 = DummyData.createItemDto((long) TEST_NUT_NO_1, 2);
//
//        // 상품 2: 가격 5000원, 재고 30개, 주문 수량 5개
//        Supplement supplement2 = DummyData.createSupplement(TEST_NUT_NO_2, 5000, 30);
//        OrderItemDTO itemDto2 = DummyData.createItemDto((long) TEST_NUT_NO_2, 5);
//
//        // 요청 DTO
//        OrderCreateRequestDTO requestDto = DummyData.createOrderRequestDTO(Arrays.asList(itemDto1, itemDto2));
//
//        // Mocking 설정: Repository 호출 시 Mock 객체 반환
//        when(supplementRepository.findByNutNoWithLock((long) TEST_NUT_NO_1))
//                .thenReturn(Optional.of(supplement1));
//        when(supplementRepository.findByNutNoWithLock((long) TEST_NUT_NO_2))
//                .thenReturn(Optional.of(supplement2));
//
//        // Order 객체를 캡처하기 위한 ArgumentCaptor
//        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
//
//        // 2. When (실행)
//        orderService.createOrder(requestDto /*, TEST_USER_UUID */); // userId 확인 로직은 주석 처리되어 있어 제외
//
//        // 3. Then (검증)
//        // 3-1. DB 저장 검증: orderRepository.save()가 한 번 호출되었는지 확인
//        verify(orderRepository, times(1)).save(orderCaptor.capture());
//
//        // 3-2. 재고 감소 검증
//        // 상품 1: 50 - 2 = 48
//        assertThat(supplement1.getStock()).isEqualTo(48);
//        // 상품 2: 30 - 5 = 25
//        assertThat(supplement2.getStock()).isEqualTo(25);
//
//        // 3-3. Order 객체 내용 검증
//        Order savedOrder = orderCaptor.getValue();
//        int expectedTotal = (10000 * 2) + (5000 * 5); // 20000 + 25000 = 45000
//
//        assertThat(savedOrder.getTotalPrice()).isEqualTo(expectedTotal);
//        assertThat(savedOrder.getUserNo()).isEqualTo(TEST_USER_UUID.toString());
//        assertThat(savedOrder.getOrderDetails()).hasSize(2);
//
//        // 3-4. OrderDetail 내용 검증 (연관관계 설정 확인)
//        savedOrder.getOrderDetails().forEach(detail -> {
//            // 모든 상세가 Order 객체를 참조하는지 확인 (연관관계 설정 확인)
//            assertThat(detail.getOrder()).isEqualTo(savedOrder);
//        });
//    }
//
//    @Test
//    @DisplayName("실패: 재고가 부족할 경우 재고 감소 시 예외가 발생해야 한다")
//    void createOrder_Failure_InsufficientStock() {
//        // 1. Given (준비)
//        // 상품: 가격 10000원, 재고 5개, 주문 수량 10개 (재고 부족 상황)
//        Supplement supplement = DummyData.createSupplement(TEST_NUT_NO_1, 10000, 5);
//        OrderItemDTO itemDto = DummyData.createItemDto((long) TEST_NUT_NO_1, 10);
//        OrderCreateRequestDTO requestDto = DummyData.createOrderRequestDTO(Arrays.asList(itemDto));
//
//        when(supplementRepository.findByNutNoWithLock((long) TEST_NUT_NO_1))
//                .thenReturn(Optional.of(supplement));
//
//        // 2. When & 3. Then (실행 및 검증)
//        // 재고 감소 로직(supplement.decreaseStock)에서 예외가 발생해야 함
//        assertThrows(IllegalArgumentException.class, () -> {
//            orderService.createOrder(requestDto);
//        }, "재고가 부족합니다");
//
//        // 예외 발생 시 주문 저장은 호출되지 않아야 함
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//    @Test
//    @DisplayName("실패: 요청 상품이 Repository에 존재하지 않을 경우 예외가 발생해야 한다")
//    void createOrder_Failure_SupplementNotFound() {
//        // 1. Given (준비)
//        OrderItemDTO itemDto = DummyData.createItemDto((long) TEST_NUT_NO_1, 2);
//        OrderCreateRequestDTO requestDto = DummyData.createOrderRequestDTO(Arrays.asList(itemDto));
//
//        // Mocking 설정: Repository 호출 시 Optional.empty() 반환
//        when(supplementRepository.findByNutNoWithLock((long) TEST_NUT_NO_1))
//                .thenReturn(Optional.empty());
//
//        // 2. When & 3. Then (실행 및 검증)
//        assertThrows(IllegalArgumentException.class, () -> {
//            orderService.createOrder(requestDto);
//        }, "해당하는 상품이 존재하지 않습니다.");
//
//        // 예외 발생 시 주문 저장은 호출되지 않아야 함
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//    // 주석 처리된 userId 검증 로직을 살릴 경우의 테스트 예시
////    @Test
////    @DisplayName("실패: 요청한 사용자 번호와 인증된 사용자 ID가 일치하지 않을 경우 예외 발생")
////    void createOrder_Failure_UserIdMismatch() {
////        // 1. Given
////        UUID wrongUserId = UUID.randomUUID();
////        OrderItemDTO itemDto = DummyData.createItemDto(TEST_NUT_NO_1, 1);
////        OrderRequestDTO requestDto = DummyData.createOrderRequestDTO(Arrays.asList(itemDto));
////
////        // 2. When & 3. Then
////        assertThrows(IllegalAccessError.class, () -> {
////            // 요청 DTO의 userNo와 다른 ID를 인자로 넘김
////            orderService.createOrder(requestDto, wrongUserId);
////        }, "주문자와 회원 ID가 일치하지 않습니다.");
////
////        verify(orderRepository, never()).save(any(Order.class));
////    }
//}