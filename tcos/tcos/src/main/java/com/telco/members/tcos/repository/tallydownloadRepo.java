package com.telco.members.tcos.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.downloaddatetime;

import jakarta.transaction.Transactional;

public interface tallydownloadRepo extends JpaRepository <downloaddatetime, Integer>{
    @Modifying
    @Transactional
    @Query(value = "UPDATE `tallytcossync` SET `download_date`=:download_date", nativeQuery = true)
    int updatedownloaddate(@Param("download_date") LocalDateTime download_date);
}