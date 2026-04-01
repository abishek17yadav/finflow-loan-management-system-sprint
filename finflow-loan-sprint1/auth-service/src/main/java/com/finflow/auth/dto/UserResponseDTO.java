package com.finflow.auth.dto;

import com.finflow.auth.entity.Role;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String fatherName;
    private String email;
    private String phone;
    private String address;
    private String occupation;
    private String aadhar;
    private Role role;
}
