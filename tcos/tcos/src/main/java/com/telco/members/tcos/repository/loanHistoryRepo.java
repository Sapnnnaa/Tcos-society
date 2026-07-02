package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.LoanType;
import com.telco.members.tcos.model.loanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface loanHistoryRepo extends JpaRepository<loanHistory, Integer> {

    loanHistory findTopByUserIdAndLoantypeOrderByLoanDateDesc(String userId, LoanType loanType);

    loanHistory findByUserIdAndLoantypeAndLoanDateBetween(String userId, LoanType loanType, Date startDate, Date endDate);

    List<loanHistory> findByUserId(String userId);

    @Query(value = """
    (SELECT * FROM loan_history 
     WHERE user_id = :userId AND loantype = 0 
     ORDER BY loan_date DESC 
     LIMIT 1)
    UNION ALL
    (SELECT * FROM loan_history 
     WHERE user_id = :userId AND loantype = 1 
     ORDER BY loan_date DESC 
     LIMIT 1)
    """, nativeQuery = true)
    List<loanHistory> findLatestLoanType0And1ByUserId(@Param("userId") String userId);
}
