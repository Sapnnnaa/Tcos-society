package com.telco.members.tcos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.members.tcos.model.DateOfEmiReport;

public interface DateOfEmiReportRepository extends JpaRepository<DateOfEmiReport, Integer> {
	
	@Query("SELECT d FROM DateOfEmiReport d")
	List<DateOfEmiReport> findAll();
	
	@Query("SELECT d FROM DateOfEmiReport d " +
		       "WHERE d.companyId = :companyId " +
		       "ORDER BY d.dateOfReport DESC")
		DateOfEmiReport findLatestReportByCompany(@Param("companyId") int companyId);
	

}
