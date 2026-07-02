package com.telco.members.tcos.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.RecoveryData;
import com.telco.members.tcos.repository.RecoveryDataRepository;

@Service
public class RecoveryDataService {

    @Autowired
    public RecoveryDataRepository recoveryDataRepository;
    @Autowired
    private AsyncMethod asyncMethod;

    public void saveAll(List<RecoveryData> data) {
        recoveryDataRepository.saveAll(data);
    }

    public void recoveryDueCheck(Date date,int companyId) {
        asyncMethod.recoveryData(date,companyId);
    }

}