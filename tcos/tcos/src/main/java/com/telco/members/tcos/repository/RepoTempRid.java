package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.TempRid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepoTempRid extends JpaRepository<TempRid, Integer> {

    @Query(value = "SELECT t_rid FROM temp_rid ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}
