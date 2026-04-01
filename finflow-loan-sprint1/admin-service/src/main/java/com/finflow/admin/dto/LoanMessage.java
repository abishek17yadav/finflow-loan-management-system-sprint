package com.finflow.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanMessage {
    private Long id;
    private String username;
    private Double amount;
    private String purpose;
    private String status;
    private LocalDateTime createdAt;
}
