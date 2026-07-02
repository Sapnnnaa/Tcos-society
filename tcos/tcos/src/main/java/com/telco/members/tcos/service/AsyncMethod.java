package com.telco.members.tcos.service;

import com.telco.members.tcos.model.InterestOnLoan;
import com.telco.members.tcos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncMethod {

    @Autowired
    private AdviceRepository adviceRepository;
    @Autowired
    private MemberReceiptRepository memberReceiptRepository;
    @Autowired
    private MemberVoucherRepository memberVoucherRepository;
    @Autowired
    private InterestOnLoanRepository interestOnLoanRepository;
    @Autowired
    private RecoveryDataRepository recoveryDataRepository;
    @Autowired
    private  Bankrepository bankrepository;


    @Async
    public CompletableFuture<Void> runProcessAdviceAsync(String bondId) {
        adviceRepository.callProcessAdvice(bondId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessCashierAsync(String bondId) {
        adviceRepository.callProcessCashier(bondId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessCancleAdviceAsync(String bondId) {
        adviceRepository.callProcessCancleAdvice(bondId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessCancleAdviceAsync2(String bondId) {
        adviceRepository.callProcessCancleAdvice2(bondId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessReceiptAsync(String receiptId) {
        memberReceiptRepository.callProcessReceipt(receiptId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessVoucherAsync(String voucherId) {
        memberVoucherRepository.callProcessVoucher(voucherId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessChasierVoc(String vid) {
        memberVoucherRepository.callProcessCashier1(vid);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessCancelVoc(String vid) {
        memberVoucherRepository.callProcessCancelVoc(vid);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runProcessCancelVoc2(String vid) {
        memberVoucherRepository.callProcessCancelVoc2(vid);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> runCreateBondId(int loanId) {
        adviceRepository.callCreateBondId(loanId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> getProxyVid(String vid) {
        memberVoucherRepository.setLatestVid(vid);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> getProxyRid(String rid) {
        memberReceiptRepository.setLatestRid(rid);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> recoveryData(Date date, int companyId) {
        recoveryDataRepository.recoveryDueCheck(date,companyId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> callProcessbanks(String p_userId,String p_accountnum,String p_bankname,String p_branchname,String p_ifsccode,String p_payableat) {
    	bankrepository.callProcessbanks(p_userId,p_accountnum,p_bankname,p_branchname,p_ifsccode,p_payableat);
        return CompletableFuture.completedFuture(null);
    }
    @Async
    public CompletableFuture<List<InterestOnLoan>> getOrGenerateInterestAsync(
            int month, int companyId, int year, int day) {
        try {
            List<InterestOnLoan> result = interestOnLoanRepository.getOrGenerateInterst(month, companyId, year, day);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            CompletableFuture<List<InterestOnLoan>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
}
