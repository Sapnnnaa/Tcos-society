package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Surety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@Repository
public interface suretiesReportRepository extends JpaRepository<Surety, Integer> {


}