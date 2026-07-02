package com.telco.members.tcos.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.telco.members.tcos.dto.loanInfoDto;
import com.telco.members.tcos.repository.LoanInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.LoanType;
import com.telco.members.tcos.model.loanHistory;
import com.telco.members.tcos.repository.loanHistoryRepo;

@Service
public class LoanHistoryService {

    @Autowired
    private loanHistoryRepo loanHistoryRepository;
    @Autowired
    private LoanInfoRepository loanInfoRepository;

    public loanHistory getLatestLoan(String userId, LoanType loanType) {
        return loanHistoryRepository.findTopByUserIdAndLoantypeOrderByLoanDateDesc(userId, loanType);
    }

    public List<loanInfoDto> getAllLoansAC(String userId){
        return loanHistoryRepository.findLatestLoanType0And1ByUserId(userId)
                .stream()
                .map(this::closureCal)
                .collect(Collectors.toList());
    }

    public double intCal1(double amount){
        double intpercent = loanInfoRepository.getInterestOnNormal();
        LocalDate today = LocalDate.now();
        int days = today.getDayOfMonth();
        return (amount * intpercent * days)/36000;
    }

    public double intCal2(double amount){
        double intpercent = loanInfoRepository.getinterestOnCommodity();
        LocalDate today = LocalDate.now();
        int days = today.getDayOfMonth();
        return (amount * intpercent * days)/36000;
    }

    private loanInfoDto closureCal(loanHistory lH) {
        loanInfoDto ac = new loanInfoDto();
        ac.setLoanId(lH.getLoanId());
        LoanType l1 = lH.getLoantype();
        if(l1 == LoanType.PERSONAL){
            double interest = intCal1(lH.getLoanBalance());
            ac.setLoanInterest(interest);
        }
        else {
            double interest = intCal2(lH.getLoanBalance());
            ac.setLoanInterest(interest);
        }
        ac.setLoantype(lH.getLoantype());
        ac.setLoanBalance(lH.getLoanBalance());
        ac.setLoanDate(lH.getLoanDate());
        return ac;
    }

    public List<loanHistory> getAllLoans(String userId){
        return loanHistoryRepository.findLatestLoanType0And1ByUserId(userId);
    }

    public loanHistory findBetweenDates(String userId, LoanType loanType, Date startDate, Date endDate) {
        return loanHistoryRepository.findByUserIdAndLoantypeAndLoanDateBetween(userId, loanType, startDate, endDate);
    }

}