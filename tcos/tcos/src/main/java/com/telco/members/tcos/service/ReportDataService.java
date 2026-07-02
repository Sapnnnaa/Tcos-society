package com.telco.members.tcos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.ReportData;
import com.telco.members.tcos.repository.ReportDataRepository;

@Service
public class ReportDataService {

    @Autowired
    private ReportDataRepository reportDataRepository;

    public List<ReportData> getByMonth(int month,int companyid,int year){
        return reportDataRepository.getReportOfMonth(month,companyid,year);
    }

}