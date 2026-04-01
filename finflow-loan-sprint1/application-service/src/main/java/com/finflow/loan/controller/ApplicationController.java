package com.finflow.loan.controller;

import com.finflow.loan.dto.LoanRequest;
import com.finflow.loan.dto.LoanResponse;
import com.finflow.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final LoanService loanService;

    // 1. Create Draft Application
    @PostMapping
    public ResponseEntity<LoanResponse> createDraft(
            @RequestHeader("loggedInUser") String username,
            @RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.createDraft(username, request));
    }

    // 2. Update Draft / Wizard Steps
    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> updateApplication(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id,
            @RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.updateApplication(username, id, request));
    }

    // 3. Submit Application
    @PostMapping("/{id}/submit")
    public ResponseEntity<LoanResponse> submitApplication(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id) {
        return ResponseEntity.ok(loanService.updateLoanStatus(id, "SUBMITTED", username));
    }

    // 4. Get My Applications
    @GetMapping("/my")
    public ResponseEntity<List<LoanResponse>> getMyApplications(
            @RequestHeader("loggedInUser") String username) {
        return ResponseEntity.ok(loanService.getMyLoans(username));
    }

    // 5. Get Application Details
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getApplication(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id) {
        return ResponseEntity.ok(loanService.getApplicationById(id, username));
    }

    // 6. Track Status Timeline
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> trackStatus(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id) {
        return ResponseEntity.ok(loanService.getTimelineStatus(id, username));
    }

    // 7. Delete Draft Application
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDraft(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id) {
        loanService.deleteApplication(id, username);
        return ResponseEntity.ok("Draft permanently deleted.");
    }

    // 8. Cancel Application
    @PutMapping("/{id}/cancel")
    public ResponseEntity<LoanResponse> cancelApplication(
            @RequestHeader("loggedInUser") String username,
            @PathVariable Long id) {
        return ResponseEntity.ok(loanService.updateLoanStatus(id, "CANCELLED", username));
    }

    // 9. Landing Page Products (Public via Gateway if configured)
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getProducts() {
        return ResponseEntity.ok(loanService.getAvailableProducts());
    }

    // 10. EMI Calculator
    @PostMapping("/calculate-emi")
    public ResponseEntity<Map<String, Double>> calculateEmi(@RequestBody Map<String, Double> payload) {
        return ResponseEntity.ok(loanService.calculateEmi(payload));
    }

    // 11. Admin - Get All Applications
    @GetMapping("/all")
    public ResponseEntity<List<LoanResponse>> getAllApplications(@RequestHeader("userRole") String role) {
        if (!"ADMIN".equals(role)) throw new RuntimeException("Access Denied: Admins Only");
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // 12. Admin - Update Application Status
    @PutMapping("/{id}/status")
    public ResponseEntity<LoanResponse> updateApplicationStatusAdmin(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("userRole") String role) {
        if (!"ADMIN".equals(role)) throw new RuntimeException("Access Denied: Admins Only");
        return ResponseEntity.ok(loanService.updateLoanStatusAdmin(id, status));
    }
}
