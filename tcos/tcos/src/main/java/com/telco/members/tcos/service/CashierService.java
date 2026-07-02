package com.telco.members.tcos.service;

import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.AdviceRepository;
import com.telco.members.tcos.repository.CashierRepo;
import com.telco.members.tcos.repository.MemberVoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class CashierService {

    @Autowired
    private CashierRepo cashierRepo;
    @Autowired
    private AdviceRepository adviceRepository;
    @Autowired
    private MemberVoucherRepository memberVoucherRepository;

    public List<Cashier> findByBLeasureType(leasureType bnd) {
        return cashierRepo.findByleasureType(bnd);
    }

    public List<Cashier> findByBLeasureTypeAndStatus(leasureType bnd, LoanStatus ls) {
        return cashierRepo.findByLeasureTypeAndCashStatus(bnd, ls);
    }

    public List<Cashier> findByVLeasureType(leasureType bnd) {
        return cashierRepo.findByleasureType(bnd);
    }

    public List<Cashier> findByVLeasureTypeAndStatus(leasureType bnd, LoanStatus ls) {
        return cashierRepo.findByLeasureTypeAndCashStatus(bnd, ls);
    }

    public void updatebond(int id) {
        String uid = cashierRepo.getUid(id);
        Date d1 = Date.valueOf(LocalDate.now());
        paymentStatus p1 = paymentStatus.Posted;
        adviceRepository.updateCashAdvice(uid, d1, p1);
        System.out.print("Updating advice cashier approved "+uid);
        cashierRepo.updateCashier(id);
    }

    public void updatevoucher(int id) {
        String uid = cashierRepo.getUid(id);
        Date d1 = Date.valueOf(LocalDate.now());
        paymentStatus p1 = paymentStatus.Posted;
        memberVoucherRepository.updateCashVoucher(uid, d1, p1);
        System.out.print("Updating voucher cashier approved "+uid);
        cashierRepo.updateCashier(id);
    }
}
