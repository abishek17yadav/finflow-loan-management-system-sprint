package com.finflow.document.dto;

import lombok.Data;

@Data
public class DocumentResponse {
    private Long id;
    private Long loanId;
    private String fileName;
    private String fileType;
}
