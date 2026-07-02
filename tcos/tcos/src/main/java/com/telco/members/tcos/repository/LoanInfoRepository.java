package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.LoanInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanInfoRepository extends JpaRepository<LoanInfo, Integer> {

    LoanInfo findById(int id);

    @Query(value = "SELECT l.interest_on_normal FROM loan_info l WHERE l.id = 1", nativeQuery = true)
    Double getInterestOnNormal();

    @Query(value = "SELECT l.interest_on_commodity FROM loan_info l WHERE l.id = 1", nativeQuery = true)
    double getinterestOnCommodity();
}
