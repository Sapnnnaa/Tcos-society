package com.telco.members.tcos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.AuditDetSettlement;

public interface AuditDetRepo extends JpaRepository <AuditDetSettlement,Integer>{
	    @Modifying
	    @Query(value="CALL AuditdetailsID(:p_userId,:p_audityear)",nativeQuery = true)
	    List<AuditDetSettlement> getAuditDetdata(@Param("p_userId") String p_userId,@Param("p_audityear") String p_audityear);
	    
	    @Query(value="CALL AuditdetailsID(:p_userId,:p_audityear)",nativeQuery = true)	    
	    AuditDetSettlement getAuditsummary(@Param("p_userId") String p_userId,@Param("p_audityear") String p_audityear);
	    
}
