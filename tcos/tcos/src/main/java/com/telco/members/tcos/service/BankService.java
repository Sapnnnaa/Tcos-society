package com.telco.members.tcos.service;

import com.telco.members.tcos.dto.BankDTO;
import com.telco.members.tcos.model.bankBalance;
import com.telco.members.tcos.model.tcosBalance;
import com.telco.members.tcos.model.transactionHistory;
import com.telco.members.tcos.repository.balanceRepo;
import com.telco.members.tcos.repository.tcosBalanceRepo;
import com.telco.members.tcos.repository.transactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.Banks;
import com.telco.members.tcos.repository.Bankrepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankService {

	@Autowired
	private tcosBalanceRepo tcosBalanceRepo;
	@Autowired
	private transactionRepo transactionRepo;
	@Autowired
	private balanceRepo balanceRepo;
	@Autowired
	private Bankrepository bankrepository;
	
	public Banks findByUserId(String userId) {
		return bankrepository.findByUserId(userId);
	}

	public List<bankBalance> findAllData() {
		return balanceRepo.findAll();
	}

	public Banks getByUserId(String userId) {
		// TODO Auto-generated method stub
		return bankrepository.findByUserId(userId);
	}
//	private BankDTO BBDto(bankBalance tb) {
//		BankDTO bd = new BankDTO();
//		bd.setTransactionId(tb.getTransactionId());
//		bd.setBankName(tb.getBankName());
//		bd.setCategory(tb.getCategory());
//		bd.setLeasuretype(tb.getLeasuretype());
//		bd.setUserId(tb.getUserId());
//		bd.setPaymenttype(tb.getPaymenttype());
//		bd.setUserId(tb.getUserId());
//		bd.setAmount(tb.getAmount());
//		bd.setBalance(tb.getBalance());
//		bd.setTransactionDate(tb.getTransactionDate());
//		return bd;
//	}

	public List<transactionHistory> getAllData() {
		return transactionRepo.findAll();
	}

	public List<Double> findAllBalance() {
		List<tcosBalance> balances = tcosBalanceRepo.findAll();

		double sbiBalance = 0.0;
		double cbiBalance = 0.0;
		double jscbBalance = 0.0;

		for (tcosBalance balance : balances) {
			String name = balance.getBankName();
			if (name.contains("State Bank Of India, Telco")) {
				sbiBalance = balance.getBankBalance();
			} else if (name.contains("Central Bank Of India, Telco")) {
				cbiBalance = balance.getBankBalance();
			} else if (name.contains("Jharkhand State Co-operative Bank, Telco")) {
				jscbBalance = balance.getBankBalance();
			}
		}

		return Arrays.asList(sbiBalance, cbiBalance, jscbBalance);
	}

//	private BankDTO TBDto(transactionHistory tb) {
//		BankDTO bd = new BankDTO();
//		bd.setTransactionId(tb.getTransactionId());
//		bd.setTransactionName(tb.getTransactionName());
//		bd.setName(tb.getName());
//		bd.setCategory(tb.getCategory());
//		bd.setLeasuretype(tb.getLeasuretype());
//		bd.setUserId(tb.getUserId());
//		bd.setPaymenttype(tb.getPaymenttype());
//		bd.setUserId(tb.getUserId());
//		bd.setAmount(tb.getAmount());
//		bd.setBalance(tb.getBalance());
//		bd.setTransactionDate(tb.getTransactionDate());
//		return bd;
//	}
}
