package com.finflow.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "application-service")
public interface ApplicationServiceClient {

    @GetMapping("/applications")
    ResponseEntity<Object> getMyLoans(@RequestHeader("userRole") String role);

    @GetMapping("/applications/all")
    ResponseEntity<Object> getAllApplications(@RequestHeader("userRole") String role);

    @PutMapping("/applications/{id}/status")
    ResponseEntity<Object> updateLoanStatus(@RequestHeader("userRole") String role, @PathVariable("id") Long id, @RequestParam("status") String status);
}
