package com.vitallog.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CartDTO {

    private Integer cartNo;
    private String userNo;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
