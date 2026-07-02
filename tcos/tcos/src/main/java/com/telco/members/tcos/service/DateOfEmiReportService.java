package com.telco.members.tcos.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.DateOfEmiReport;
import com.telco.members.tcos.repository.DateOfEmiReportRepository;

@Service
public class DateOfEmiReportService {
	
	@Autowired
	DateOfEmiReportRepository dateOfEmiReportRepository;
	
	public List<DateOfEmiReport> getAllReportDate(){
		return dateOfEmiReportRepository.findAll();
	}
	
	public Date getLastDate(int companyId) {
		
		DateOfEmiReport report=dateOfEmiReportRepository.findLatestReportByCompany(companyId);
		
		return report.getDateOfReport();
	}

}
