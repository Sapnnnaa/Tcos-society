package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.TempVid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RepoTempVid extends JpaRepository<TempVid, Integer> {

    @Query(value = "SELECT t_vid FROM temp_vid ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}
