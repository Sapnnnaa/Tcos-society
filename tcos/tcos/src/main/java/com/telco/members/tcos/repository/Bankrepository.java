package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Banks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface Bankrepository extends JpaRepository<Banks, Integer>{

    Banks findByUserId(String userId);

    @Query(value = "SELECT bank_name FROM banks WHERE user_id = :userId", nativeQuery = true)
    String getBankNameByuserId(@Param("userId")String useerId);

    @Query(value = "SELECT account_num FROM banks WHERE user_id = :userId", nativeQuery = true)
    String getAccountNumByuserId(@Param("userId")String useerId);
    
    @Procedure(procedureName = "Insertorupdatebanks")
    void callProcessbanks(@Param("p_userId") String p_userId,@Param("p_accountnum") String p_accountnum,@Param("p_bankname") String p_bankname,@Param("p_branchname") String p_branchname,@Param("p_ifsccode") String p_ifsccode,@Param("p_payableat") String p_payableat);
}
