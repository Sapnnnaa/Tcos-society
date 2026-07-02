package com.telco.members.tcos.service;

import com.telco.members.tcos.model.Surety;
import com.telco.members.tcos.model.User;
import com.telco.members.tcos.repository.SuretyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class SuretyService {

    @Autowired
    UserService userService;
    @Autowired
    SuretyRepository suretyRepository;

    public void addSurety(String userId, String userIds) {
        Surety gua = new Surety();
        User user = userService.findByUserId(userIds);
        gua.setUserId(userId);
        gua.setG_userId(userIds);
        gua.setG_name(user.getName());
        gua.setPhone_num(user.getPhoneNum());
        gua.setApprovedate(Date.valueOf(LocalDate.now()));
        suretyRepository.save(gua);
    }

    public void removeSurety(String userId, String g_userId) {
        suretyRepository.deleteByg_userId(userId, g_userId);
    }

    public List<Surety> findByUserId(String userId) {
        return suretyRepository.findByUserId(userId);
    }
}
