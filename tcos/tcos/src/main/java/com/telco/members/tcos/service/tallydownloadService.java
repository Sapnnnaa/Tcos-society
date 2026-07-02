package com.telco.members.tcos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.repository.tallydownloadRepo;
import com.telco.members.tcos.model.tallydownloads;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class tallydownloadService {
    @Autowired
    private tallydownloadRepo tallydownloadrepo;

    public int getbanktranstallydata(LocalDateTime download_date) {
        return tallydownloadrepo.updatedownloaddate(download_date);
    }
}
