package com.telco.members.tcos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.ReportData;

public interface ReportDataRepository extends JpaRepository<ReportData, Integer> {

    @Query(value = "SELECT * FROM report_data WHERE company_id=:companyId AND MONTH(created_for)=:month AND YEAR(created_for)=:year", nativeQuery = true)
    List<ReportData> getReportOfMonth(@Param("month") int month, @Param("companyId") int companyId,  @Param("year") int year);


}