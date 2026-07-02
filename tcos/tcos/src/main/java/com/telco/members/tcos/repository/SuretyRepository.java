package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Surety;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuretyRepository extends JpaRepository<Surety,Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Surety u WHERE u.g_userId = :g_userId AND u.userId = :userId")
    void deleteByg_userId(@Param("userId") String userId, @Param("g_userId") String g_userId);

    List<Surety> findByUserId(String userId);
}
