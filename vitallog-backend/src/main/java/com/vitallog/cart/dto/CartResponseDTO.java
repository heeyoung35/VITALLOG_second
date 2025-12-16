package com.vitallog.cart.dto;


import com.vitallog.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartResponseDTO {
    private Integer cartNo;
    private String userNo;
    private LocalDateTime createdAt;

    public void setUser(User user) {
        if(user != null) {
            this.userNo = user.getUserNo();
        }
    }
}
