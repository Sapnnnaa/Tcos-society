package com.telco.members.tcos.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.members.tcos.model.MemberReceipt;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface MemberReceiptRepository extends JpaRepository<MemberReceipt, Integer> {

    List<MemberReceipt> findByStage(int stage);

    @Procedure(procedureName = "ProcessReceipt")
    void callProcessReceipt(@Param("input_rid") String receiptId);

    @Modifying
    @Transactional
    void deleteByrId(String vId);

    MemberReceipt findByrId(String rId);

    @Procedure(procedureName = "AssignRECID")
    void setLatestRid(@Param("input_id") String rid);
//    @Query(value = "SELECT r_id FROM member_receipt mr ORDER BY mr.r_id DESC LIMIT 1", nativeQuery = true)
//    Optional<String> findLatestrid();

    @Query(value = "SELECT m.r_id, m.category, m.user_id, m.name, v.vouchertype, v.amount, m.approve_date " +
            "FROM member_receipt m JOIN receipt v ON m.r_id = v.v_id " +
            "WHERE m.stage = 2", nativeQuery = true)
    List<Object[]> findApprovedReportRaw();
}
