package com.finflow.loan.service;

import com.finflow.loan.dto.LoanRequest;
import com.finflow.loan.dto.LoanResponse;
import com.finflow.loan.entity.LoanApplicationEntity;
import com.finflow.loan.repository.LoanRepository;
import com.finflow.loan.exception.ResourceNotFoundException;
import com.finflow.loan.exception.UnauthorizedException;
import com.finflow.loan.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private LoanApplicationEntity testEntity;
    private LoanRequest testRequest;
    private final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        testEntity = LoanApplicationEntity.builder()
                .id(1L)
                .username(TEST_USERNAME)
                .amount(BigDecimal.valueOf(50000))
                .purpose("Personal Loan")
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = new LoanRequest();
        testRequest.setAmount(BigDecimal.valueOf(50000));
        testRequest.setPurpose("Personal Loan");
    }

    @Test
    void testCreateDraft() {
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(testEntity);

        LoanResponse response = loanService.createDraft(TEST_USERNAME, testRequest);

        assertNotNull(response);
        assertEquals(testEntity.getId(), response.getId());
        assertEquals(TEST_USERNAME, response.getUsername());
        assertEquals(testEntity.getAmount(), response.getAmount());
        assertEquals("DRAFT", response.getStatus());

        verify(loanRepository, times(1)).save(any(LoanApplicationEntity.class));
    }

    @Test
    void testGetMyLoans() {
        when(loanRepository.findByUsername(TEST_USERNAME)).thenReturn(Collections.singletonList(testEntity));

        List<LoanResponse> loans = loanService.getMyLoans(TEST_USERNAME);

        assertNotNull(loans);
        assertEquals(1, loans.size());
        assertEquals(testEntity.getId(), loans.get(0).getId());

        verify(loanRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void testCalculateEmi() {
        Map<String, Double> payload = Map.of(
            "principal", 100000.0,
            "rate", 12.0,
            "months", 12.0
        );

        Map<String, Double> result = loanService.calculateEmi(payload);

        assertNotNull(result);
        assertTrue(result.containsKey("emi"));
        double emi = result.get("emi");
        assertTrue(emi > 8800 && emi < 8900);
    }

    @Test
    void testGetLoanById_NotFound() {
        when(loanRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.getApplicationById(1L, TEST_USERNAME);
        });
    }

    @Test
    void testGetLoanById_Unauthorized() {
        testEntity.setUsername("otheruser");
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testEntity));

        assertThrows(UnauthorizedException.class, () -> {
            loanService.getApplicationById(1L, TEST_USERNAME);
        });
    }

    @Test
    void testDelete_NonDraft() {
        testEntity.setStatus("SUBMITTED");
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testEntity));

        assertThrows(BadRequestException.class, () -> {
            loanService.deleteApplication(1L, TEST_USERNAME);
        });
    }
}
