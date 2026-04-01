package com.finflow.loan.repository;

import com.finflow.loan.entity.LoanApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplicationEntity, Long> {
    List<LoanApplicationEntity> findByUsername(String username);
}
