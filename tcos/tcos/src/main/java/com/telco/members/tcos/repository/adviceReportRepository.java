package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.LoanVoucher;   // FIXED IMPORT — REQUIRED
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface adviceReportRepository extends JpaRepository<LoanVoucher, Integer> {
}
