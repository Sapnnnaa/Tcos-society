package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.AccessControl;
import com.telco.members.tcos.model.accessDec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccessCtrlRepo extends JpaRepository<AccessControl, Integer> {

    @Query(value = "SELECT access_id FROM access_control WHERE access_declare = :accessId", nativeQuery = true)
    int getAccessId(@Param("accessId") accessDec accessId);
}
