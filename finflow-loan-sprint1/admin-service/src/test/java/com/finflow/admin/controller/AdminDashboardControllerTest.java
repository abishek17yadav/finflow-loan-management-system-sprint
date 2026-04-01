package com.finflow.admin.controller;

import com.finflow.admin.client.ApplicationServiceClient;
import com.finflow.admin.client.AuthServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDashboardController.class)
public class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationServiceClient applicationServiceClient;

    @MockBean
    private AuthServiceClient authServiceClient;

    @Test
    void testGetDashboardStats_AsAdmin() throws Exception {
        // Mocking the Feign client response
        ResponseEntity<Object> mockResponse = ResponseEntity.ok(List.of(
                Map.of("id", 1, "status", "APPROVED")
        ));
        
        when(applicationServiceClient.getMyLoans(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/dashboard")
                .header("userRole", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dashboard loaded successfully"))
                .andExpect(jsonPath("$.loans").exists());
    }

    @Test
    void testGetDashboardStats_AsApplicant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                .header("userRole", "APPLICANT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetReports_AsAdmin() throws Exception {
        mockMvc.perform(get("/admin/reports")
                .header("userRole", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(150))
                .andExpect(jsonPath("$.approved").value(80));
    }
}
