package com.telco.members.tcos.service;

import com.telco.members.tcos.model.Guarantor;
import com.telco.members.tcos.model.Tcosbanks;
import com.telco.members.tcos.model.User;
import com.telco.members.tcos.repository.GurantorRepository;
import com.telco.members.tcos.repository.TcosbankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuarantorService {

    @Autowired
    UserService userService;
    @Autowired
    GurantorRepository gurantorRepository;

    private final ConcurrentHashMap<String, List<Guarantor>> userGuarantorMap = new ConcurrentHashMap<>();

    // Add a guarantor to a user
    public void addGuarantor(String userId, int loanId, String userIds) {
        Guarantor gua = new Guarantor();
        User user = userService.findByUserId(userIds);
        gua.setLoanId(loanId);
        gua.setUserId(userId);
        gua.setG_userId(userIds);
        gua.setG_name(user.getName());
        gua.setPhone_num(user.getPhoneNum());
        gua.setApprovedate(Date.valueOf(LocalDate.now()));
        gua.setG_value(0);
        gurantorRepository.save(gua);
    }

    public List<Guarantor> getGuarantors(int loanId) {
        return gurantorRepository.findByloanId(loanId);
    }


    // Remove a guarantor for a user
    public void removeGuarantor( int loanId, String userId, String g_userId) {
        gurantorRepository.deleteByg_userId(loanId, userId, g_userId);
    }

    public int getGuarantorCount(String gUserId) {
        return gurantorRepository.countByGUserId(gUserId);
    }

    // Check if a guarantor exists
    public boolean hasGuarantor(String userId, String g_userId) {
        List<Guarantor> guarantors = userGuarantorMap.get(userId);
        return guarantors != null && guarantors.stream().anyMatch(g -> g.getG_userId() == g_userId);
    }

	public List<Guarantor> findByUserId(String userId) {
		// TODO Auto-generated method stub
		return gurantorRepository.findByUserId(userId);
	}
}

