package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Guarantor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GurantorRepository extends JpaRepository<Guarantor,Integer> {


    @Modifying
    @Transactional // Required for modifying queries
    @Query("DELETE FROM Guarantor u WHERE u.g_userId = :g_userId AND u.userId = :userId AND u.loanId = :loanId")
    void deleteByg_userId(@Param("loanId") int loanId, @Param("userId") String userId, @Param("g_userId") String g_userId);

    List<Guarantor> findByloanId(int loanId);

	List<Guarantor> findByUserId(String userId);

    @Query(value = "SELECT COUNT(*) FROM guarantor WHERE g_user_id = :gUserId", nativeQuery = true)
    int countByGUserId(@Param("gUserId") String gUserId);
}
