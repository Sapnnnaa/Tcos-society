package com.telco.members.tcos.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.members.tcos.model.Members;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembersRepository extends JpaRepository<Members, Integer> {
	
	Members findBymid(String mid);

    @Query(value = "SELECT email FROM members WHERE mid = :mid", nativeQuery = true)
    String findEmailIdByMid(@Param("mid") String mid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE members SET password = :pass WHERE mid = :mid", nativeQuery = true)
    void updatePasswordForEmpID(@Param("mid") String mid, @Param("pass") String pass);
}
