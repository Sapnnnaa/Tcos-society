package com.telco.members.tcos.repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.AuditDetSettlement;
import com.telco.members.tcos.model.AuditDetSettlement1;

public interface AuditDetRepo1  extends JpaRepository <AuditDetSettlement1,Integer> {
	 @Modifying
	    @Query(value="CALL AuditdetailsID(:p_userId,:p_audityear)",nativeQuery = true)
	    List<AuditDetSettlement1> getAuditDetnormdata(@Param("p_userId") String p_userId,@Param("p_audityear") String p_audityear);
	 
	    @Query(value="CALL AuditdetailsID(:p_userId,:p_audityear)",nativeQuery = true)
	    AuditDetSettlement1 getAuditsummarydata(@Param("p_userId") String p_userId,@Param("p_audityear") String p_audityear);
	 
}
