package com.telco.members.tcos.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.EmiPaymentType;
import com.telco.members.tcos.model.EmiPaymentsDetails;

public interface EmiPaymentsDetailsRepository extends JpaRepository<EmiPaymentsDetails, Integer> {

	@Query("SELECT e FROM EmiPaymentsDetails e "
			+ "WHERE e.userId = :userId "
			+ "ORDER BY e.paymentDate DESC")
	List<EmiPaymentsDetails> allEmis(@Param("userId") String userId);
//to get latest Normal_deduction emi for specific userid and loanid

	@Query("SELECT e FROM EmiPaymentsDetails e " + "WHERE e.paymentDate > :lastDate AND e.loanId = :loanId")
	List<EmiPaymentsDetails> findByDateAfter(@Param("lastDate") Date lastDate, @Param("loanId") int loanId);
//get emi from date before for specific company and loan id

}
