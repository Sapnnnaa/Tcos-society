package com.telco.members.tcos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.DueRecovery;
import com.telco.members.tcos.repository.DueRecoveryRepository;

@Service
public class DueRecoveryService {

    @Autowired
    private DueRecoveryRepository dueRecoveryRepository;

    public List<DueRecovery> findAll(){

        return dueRecoveryRepository.findAll();
    }

}