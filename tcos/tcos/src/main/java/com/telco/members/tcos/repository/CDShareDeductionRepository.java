package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface CDShareDeductionRepository extends JpaRepository<CDShareDeductions, Integer> {

    @Query(value = "SELECT * FROM cd_share_deductions WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<CDShareDeductions> findByUserId(String userId);

    @Query(value = "SELECT * FROM cd_share_deductions WHERE user_id = :userId ORDER BY payment_date DESC LIMIT 1", nativeQuery = true)
    CDShareDeductions findByUserIds(String userId);

    CDShareDeductions findTopByUserIdOrderByPaymentDateDesc(String userId); 
	
	CDShareDeductions findById(int id);
	
	 @Query(value = "SELECT * FROM cd_share_deductions WHERE user_id = :userId ORDER BY payment_date DESC LIMIT 1", nativeQuery = true)
	CDShareDeductions findLatestRecordByUserId(String userId);
	 
	   @Modifying
	    @Transactional
	    @Query(value = "insert into cd_share_deductions (user_id,cd_deduction,share_deduction,payment_date,cd_deduction_generated,share_deduction_generated,cd_interest,cd_interest_total) values  (:userId, :txtcddeduct,:txtsharededuct,:Paymentdt,0,0,0,0)", nativeQuery = true)
	    void insertcddeducat(@Param("userId") String userId,@Param("txtcddeduct") String txtcddeduct, @Param("txtsharededuct") String txtsharededuct,@Param("Paymentdt") String Paymentdt);
}
