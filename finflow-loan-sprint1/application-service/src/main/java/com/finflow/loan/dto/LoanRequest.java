package com.finflow.loan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequest {
    private BigDecimal amount;
    private String purpose;
}
