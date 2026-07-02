package com.telco.members.tcos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.Banktranstally;

import java.time.LocalDate;
import java.time.LocalDateTime;
public interface BanktranstallyRepository extends JpaRepository <Banktranstally, Integer>{
    @Modifying
    @Query(value="CALL ProcessBankTransction(:transdt)",nativeQuery = true)
    List<Banktranstally> getBanktransdata(@Param("transdt") LocalDateTime transdt);
}