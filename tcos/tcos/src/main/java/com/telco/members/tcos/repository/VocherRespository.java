package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Voucher;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VocherRespository extends JpaRepository<Voucher, Integer> {

    List<Voucher> findByvId(String vId);

    @Modifying
    @Transactional
    void deleteByvId(String vId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM voucher WHERE v_id = :vId AND (v_sln = :vSln OR (:vSln IS NULL AND v_sln IS NULL))", nativeQuery = true)
    void deleteByVIdAndVSln(@Param("vId") String vId, @Param("vSln") String vSln);
}
