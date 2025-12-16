package com.vitallog.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FaqUserDTO {

    private int faqNo;
    private String category;
    private String question;
    private String answerContent;
    private LocalDateTime createdAt;
}
