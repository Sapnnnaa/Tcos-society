package com.telco.members.tcos.service;

import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.repository.CDShareDeductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.repository.CDShareDeductionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CDShareDeductionService {

    @Autowired
    private CDShareDeductionRepository cdShareDeductionRepository;

    public CDShareDeductions save(CDShareDeductions deduction) {
        return cdShareDeductionRepository.save(deduction);
    }

    public CDShareDeductions getLatestRecord(String userId) {
        return cdShareDeductionRepository.findTopByUserIdOrderByPaymentDateDesc(userId);
    }

    public List<CDShareDeductions> saveAll(List<CDShareDeductions> cdShareDeductions) {
        // TODO Auto-generated method stub
        return cdShareDeductionRepository.saveAll(cdShareDeductions);
    }
    public CDShareDeductions findById1(int id) {
        return cdShareDeductionRepository.findById(id);
    }

    public CDShareDeductions findLatestRecordByUserId(String userId) {
        return cdShareDeductionRepository.findLatestRecordByUserId(userId);
    }
    public void insertcddeducat(String userId,String txtcddeduct,String txtsharededuct,String Paymentdt) {
        cdShareDeductionRepository.insertcddeducat(userId,txtcddeduct,txtsharededuct,Paymentdt);
    }

    public List<CDShareDeductions> getSettlementData(
            String userId,
            LocalDate fromDate,
            LocalDate toDate) {

        return cdShareDeductionRepository.findSettlementData(
                userId,
                fromDate,
                toDate);
    }
}