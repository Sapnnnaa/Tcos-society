package com.telco.members.tcos.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.telco.members.tcos.model.Banktranstally;
import com.telco.members.tcos.repository.BanktranstallyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class BanktranstallyService {
    @Autowired
    private BanktranstallyRepository banktranstallyRepository;

    public List<Banktranstally> getBanktranssdata(LocalDateTime transdt) {
        return banktranstallyRepository.getBanktransdata(transdt);
    }
}
