package com.telco.members.tcos.repository;

import com.telco.members.tcos.dto.LoanapproveDTO;
import com.telco.members.tcos.model.Loan;
import com.telco.members.tcos.model.LoanStatus;
import com.telco.members.tcos.model.LoanType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface loanRepository extends JpaRepository<Loan, Integer> {

    Optional<Loan> findByuserId(String userId);

    List<Loan> findByLoanTypeAndLoanStatus(LoanType loanType, LoanStatus loanStatus);

    List<Loan> findByLoanStatus(LoanStatus loanStatus);

    Optional<Loan> findByLoanTypeAndLoanStatusAndUserId(LoanType loanType, LoanStatus loanStatus, String userId);

    @Query("SELECT l FROM Loan l JOIN User u ON l.userId = u.userId WHERE l.loanStatus = :loanStatus AND u.companyId = :companyId")
    List<Loan> findByLoanStatusAndCompanyId(@Param("loanStatus") LoanStatus loanStatus, @Param("companyId") int companyId);

    Optional<Loan> findByuserIdAndLoanType(String userId, LoanType loanType);

    Loan findByUserIdAndLoanId(String userId, int loanId);

    Optional<Loan> findByLoanIdAndUserId(int loanId, String userId);

    Loan findByloanId(int loanId);

    List<Loan> findByUserIdAndLoanTypeAndLoanStatusAndLoanApplyDateBetween(String userId, LoanType loanType, LoanStatus loanStatus, Date startDate, Date endDate);

    Loan findByUserIdAndLoanTypeAndLoanStatus(String userId, LoanType loanType, LoanStatus loanStatus);

    @Query(value = "SELECT user_id FROM loans WHERE id = :adviceId", nativeQuery = true)
    String findUserIdByLoanId(@Param("adviceId") int adviceId);

    @Query(value = "SELECT user_id, loan_status, loan_apply_date, loan_type, loan_amount " +
            "FROM loans WHERE user_id = :userId AND loan_status IN (0,1,2)",
            nativeQuery = true)
    List<Object[]> findByUserIdandLoanTypeEqual(@Param("userId") String userId);

    @Query(value = "SELECT COUNT(*) FROM loans WHERE loan_status = 0", nativeQuery = true)
    int countLp();

    @Query(value = "SELECT COUNT(*) FROM loans WHERE loan_status = 3 AND YEAR(loan_approve_date) = YEAR(CURDATE()) AND MONTH(loan_approve_date) = MONTH(CURDATE())", nativeQuery = true)
    int countLP();

    @Modifying
    @Transactional
    @Query(value = "CALL loan_end();", nativeQuery = true)
    void loanEnd();
    
    List<Loan> findByUserIdAndLoanTypeAndLoanBalanceGreaterThanOrderByLoanApproveDateDesc(String userId,LoanType loanType,double amt);

    Loan findTop1ByUserIdAndLoanTypeAndLoanStatusOrderByLoanApproveDateDesc(String userId,LoanType loanType,LoanStatus loanStatus);
	Loan findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(String userId, LoanType loanType);
	
	@Query(value = "SELECT COUNT(*) FROM loans l WHERE l.user_id = :userId AND l.loan_type = :loanType AND "
			+ "(l.loan_status = 0 OR l.loan_status=1 OR l.loan_status=2)", nativeQuery = true)
	int countPendingLoans(@Param("userId") String userId, @Param("loanType") int loanType);
    
//    @Modifying
//    @Transactional // Required for modifying queries
//    @Query("DELETE FROM users u WHERE u.userId = :userId")
//    void deleteByuserId(@Param("userId") String userId);

}
