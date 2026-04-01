package com.finflow.loan.service;

import com.finflow.loan.dto.LoanRequest;
import com.finflow.loan.dto.LoanResponse;
import com.finflow.loan.entity.LoanApplicationEntity;
import com.finflow.loan.repository.LoanRepository;
import com.finflow.loan.exception.ResourceNotFoundException;
import com.finflow.loan.exception.BadRequestException;
import com.finflow.loan.exception.UnauthorizedException;
import com.finflow.loan.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final RabbitTemplate rabbitTemplate;

    public LoanResponse createDraft(String username, LoanRequest request) {
        LoanApplicationEntity entity = LoanApplicationEntity.builder()
                .username(username)
                .amount(request.getAmount())
                .purpose(request.getPurpose())
                .status("DRAFT")
                .build();
        entity = loanRepository.save(entity);
        
        // Notify RabbitMQ
        LoanResponse response = mapToResponse(entity);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, response);
        
        return response;
    }

    public LoanResponse updateApplication(String username, Long id, LoanRequest request) {
        LoanApplicationEntity entity = getEntity(id, username);
        if(!entity.getStatus().equals("DRAFT")) throw new BadRequestException("Cannot edit non-draft");
        entity.setAmount(request.getAmount());
        entity.setPurpose(request.getPurpose());
        return mapToResponse(loanRepository.save(entity));
    }

    public LoanResponse updateLoanStatus(Long id, String status, String username) {
        LoanApplicationEntity entity = getEntity(id, username);
        entity.setStatus(status);
        return mapToResponse(loanRepository.save(entity));
    }

    public List<LoanResponse> getMyLoans(String username) {
        return loanRepository.findByUsername(username).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public LoanResponse getApplicationById(Long id, String username) {
        return mapToResponse(getEntity(id, username));
    }

    public void deleteApplication(Long id, String username) {
        LoanApplicationEntity entity = getEntity(id, username);
        if(!entity.getStatus().equals("DRAFT")) throw new BadRequestException("Only drafts can be deleted");
        loanRepository.delete(entity);
    }

    public Map<String, String> getTimelineStatus(Long id, String username) {
        LoanApplicationEntity entity = getEntity(id, username);
        Map<String, String> status = new HashMap<>();
        status.put("currentStatus", entity.getStatus());
        status.put("lastUpdated", entity.getCreatedAt().toString());
        return status;
    }

    public List<Map<String, Object>> getAvailableProducts() {
        return List.of(
            Map.of("id", 1, "name", "Personal Loan", "interestRate", 10.5),
            Map.of("id", 2, "name", "Home Loan", "interestRate", 7.2)
        );
    }

    public Map<String, Double> calculateEmi(Map<String, Double> payload) {
        double p = payload.getOrDefault("principal", 0.0);
        double r = payload.getOrDefault("rate", 0.0) / (12 * 100);
        double n = payload.getOrDefault("months", 1.0);
        double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return Map.of("emi", emi);
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Used by admin
    public LoanResponse updateLoanStatusAdmin(Long id, String status) {
        LoanApplicationEntity entity = loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        entity.setStatus(status);
        return mapToResponse(loanRepository.save(entity));
    }

    private LoanApplicationEntity getEntity(Long id, String username) {
        LoanApplicationEntity entity = loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        if (!entity.getUsername().equals(username)) throw new UnauthorizedException("Unauthorized access to application");
        return entity;
    }


    private LoanResponse mapToResponse(LoanApplicationEntity entity) {
        LoanResponse response = new LoanResponse();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setAmount(entity.getAmount());
        response.setPurpose(entity.getPurpose());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
