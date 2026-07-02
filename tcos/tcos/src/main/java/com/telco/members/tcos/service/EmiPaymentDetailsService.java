package com.telco.members.tcos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.EmiPaymentType;
import com.telco.members.tcos.model.EmiPaymentsDetails;
import com.telco.members.tcos.repository.EmiPaymentsDetailsRepository;

@Service
public class EmiPaymentDetailsService {
	
	@Autowired
	private EmiPaymentsDetailsRepository emiPaymentsDetailsRepository;

	public List<EmiPaymentsDetails> allEmis(String userId) {
		return emiPaymentsDetailsRepository.allEmis(userId);
	}
	
	public List<EmiPaymentsDetails> beforeDate(Date lastDate,int loanId){
		return emiPaymentsDetailsRepository.findByDateAfter(lastDate, loanId);
	}

	public EmiPaymentsDetails save(EmiPaymentsDetails emi) {
		return emiPaymentsDetailsRepository.save(emi);
	}

	public List<EmiPaymentsDetails> saveAll(List<EmiPaymentsDetails> emiPaymentsDetails) {
		// TODO Auto-generated method stub
		return emiPaymentsDetailsRepository.saveAll(emiPaymentsDetails);
	}
}
