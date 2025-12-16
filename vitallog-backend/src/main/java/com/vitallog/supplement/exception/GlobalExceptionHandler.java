//package com.vitallog.supplement.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.View;
//
//import java.time.LocalDateTime;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    private final View error;
//
//    public GlobalExceptionHandler(View error) {
//        this.error = error;
//    }
//
//    // IllegalArgumentException 처리
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
//
//        ErrorResponse response = ErrorResponse.builder()
//                .status(HttpStatus.BAD_REQUEST.value())  // 400
//                .error(HttpStatus.BAD_REQUEST.getReasonPhrase()) // "Bad Request"
//                .message(e.getMessage())  // 서비스에서 보낸 메시지
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    // 상품 등록 미입력시 에러 메세지
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
//        String message = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .findFirst()
//                .orElse("입력 값 오류가 발생했습니다.");
//
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error("Bad_Request")
//                .message(message)
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.badRequest().body(errorResponse);
//    }
//}
