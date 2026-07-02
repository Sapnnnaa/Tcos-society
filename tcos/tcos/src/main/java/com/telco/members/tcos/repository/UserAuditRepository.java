package com.telco.members.tcos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserAuditRepository extends JpaRepository<User,Integer> {
	@Modifying
    @Query(value="CALL AuditSettlementID(:p_userId)",nativeQuery = true)
	List<User> getuserAuditdata(@Param("p_userId")String p_userId);
    
}
