package com.finflow.admin.controller;

import com.finflow.admin.client.ApplicationServiceClient;
import com.finflow.admin.client.AuthServiceClient;
import com.finflow.admin.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ApplicationServiceClient applicationServiceClient;
    private final AuthServiceClient authServiceClient;

    // 1. Dashboard Stats
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(@RequestHeader("userRole") String role) {
        validateAdmin(role);
        Map<String, Object> dashboard = new HashMap<>();
        ResponseEntity<Object> loansRes = applicationServiceClient.getMyLoans(role);
        dashboard.put("loans", loansRes.getBody());
        dashboard.put("message", "Dashboard loaded successfully");
        return ResponseEntity.ok(dashboard);
    }

    // 2. Application Queue
    @GetMapping("/applications")
    public ResponseEntity<?> getApplicationQueue(@RequestHeader("userRole") String role) {
        validateAdmin(role);
        return applicationServiceClient.getAllApplications(role);
    }

    // 3. Verify Document
    @PutMapping("/documents/{id}/verify")
    public ResponseEntity<?> verifyDocument(@RequestHeader("userRole") String role, @PathVariable Long id) {
        validateAdmin(role);
        // In reality, this communicates with document-service or application-service
        return ResponseEntity.ok(Map.of("message", "Document " + id + " verified successfully."));
    }

    // 4. Decision
    @PostMapping("/applications/{id}/decision")
    public ResponseEntity<?> makeDecision(
            @RequestHeader("userRole") String role,
            @PathVariable Long id,
            @RequestBody Map<String, String> decisionPayload) {
        validateAdmin(role);
        String status = decisionPayload.get("decision"); // APPROVED or REJECTED
        return applicationServiceClient.updateLoanStatus(role, id, status);
    }

    // 5. Reports
    @GetMapping("/reports")
    public ResponseEntity<?> getReports(@RequestHeader("userRole") String role) {
        validateAdmin(role);
        return ResponseEntity.ok(Map.of("totalApplications", 150, "approved", 80, "rejected", 20, "pending", 50));
    }

    // 6. Export Report
    @GetMapping("/reports/export")
    public ResponseEntity<?> exportReports(@RequestHeader("userRole") String role) {
        validateAdmin(role);
        return ResponseEntity.ok("CSV_DATA_GENERATED_MOCK"); // Returns mock CSV
    }

    // 7. Users Proxy
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader("userRole") String role) {
        validateAdmin(role);
        return authServiceClient.getUsers(role);
    }

    private void validateAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException("Admins Only");
        }
    }
}
