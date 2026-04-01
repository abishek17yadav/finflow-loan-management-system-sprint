package com.finflow.loan.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanResponse {
    private Long id;
    private String username;
    private BigDecimal amount;
    private String purpose;
    private String status;
    private LocalDateTime createdAt;
}
