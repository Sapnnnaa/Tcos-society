//package com.telco.members.tcos.service;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import com.telco.members.tcos.model.InterestOnLoan;
//import com.telco.members.tcos.repository.InterestOnLoanRepository;
//
//@Service
//public class InterestOnLoanSerive {
//
//    @Autowired
//    private InterestOnLoanRepository interestOnLoanRepository;
//
//    @Async("interestExecutor")
//    public CompletableFuture<List<InterestOnLoan>> getOrGenerateInterestAsync(
//            int month, int companyId, int year, int day) {
//        try {
//            List<InterestOnLoan> result = interestOnLoanRepository.getOrGenerateInterst(month, companyId, year, day);
//            return CompletableFuture.completedFuture(result);
//        } catch (Exception e) {
//            CompletableFuture<List<InterestOnLoan>> failedFuture = new CompletableFuture<>();
//            failedFuture.completeExceptionally(e);
//            return failedFuture;
//        }
//    }
//
//}