package com.telco.members.tcos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.PaymentAccountdet;
import com.telco.members.tcos.repository.PaymentAccountdetRepo;


@Service
public class PaymentAccountService {
	 @Autowired
	 private PaymentAccountdetRepo paymentAccountdetRepo;

	    public List<PaymentAccountdet> getpaymentaccountdata() {
	        return paymentAccountdetRepo.getpaymentaccountdata();
	    }
}
