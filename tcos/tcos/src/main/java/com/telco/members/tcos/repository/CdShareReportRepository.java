package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.CDShareDeductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdShareReportRepository extends JpaRepository<CDShareDeductions, Integer> {
}
