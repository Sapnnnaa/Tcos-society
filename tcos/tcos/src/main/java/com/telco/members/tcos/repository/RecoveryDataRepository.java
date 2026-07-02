package com.telco.members.tcos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.members.tcos.model.RecoveryData;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface RecoveryDataRepository extends JpaRepository<RecoveryData, Long> {

    @Procedure(procedureName = "CalculateDueRecovery")
    void recoveryDueCheck(@Param("p_forDate") Date date, @Param("p_companyId") int companyId);
}