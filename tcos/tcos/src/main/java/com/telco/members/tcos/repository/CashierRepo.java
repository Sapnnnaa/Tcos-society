package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Cashier;
import com.telco.members.tcos.model.LoanStatus;
import com.telco.members.tcos.model.leasureType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CashierRepo extends JpaRepository<Cashier, Integer> {

    @Query(value = "SELECT * FROM cashier WHERE leasure_type=:leasureType AND cash_status != 4", nativeQuery = true)
    List<Cashier> findByleasureType(@Param("leasureType") leasureType bnd) ;

    List<Cashier> findByLeasureTypeAndCashStatus(leasureType bnd, LoanStatus s1);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cashier SET cash_status = 2 WHERE id = :Id", nativeQuery = true)
    void updateCashier(@Param("Id") int id);

    @Query(value = "SELECT uid FROM cashier WHERE id = :Id", nativeQuery = true)
    String getUid(@Param("Id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cashier SET uuid = :uuid, cash_status = 4, updated_date = NOW() WHERE uid = :uId", nativeQuery = true)
    void updateBondCancelled(@Param("uId") String id, @Param("uuid") String uuid);
}

