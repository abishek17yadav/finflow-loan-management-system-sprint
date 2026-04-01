package com.finflow.auth.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String fullName;
    private String fatherName;
    private String phone;
    private String email;
    private String address;
    private String occupation;
    private String aadhar;
    private String password;
    private String role; // Optional, defaulted to APPLICANT
}
