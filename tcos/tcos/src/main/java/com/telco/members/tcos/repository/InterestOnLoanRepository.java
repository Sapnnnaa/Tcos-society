package com.telco.members.tcos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.InterestOnLoan;

public interface InterestOnLoanRepository extends JpaRepository<InterestOnLoan, Integer> {

//    @Modifying
    @Query(value="CALL interest_calculator(:month, :companyid, :year, :day)",nativeQuery = true)
    List<InterestOnLoan> getOrGenerateInterst(@Param("month") int month,
                                              @Param("companyid") int companyId,
                                              @Param("year") int year,
                                              @Param("day") int day);

}