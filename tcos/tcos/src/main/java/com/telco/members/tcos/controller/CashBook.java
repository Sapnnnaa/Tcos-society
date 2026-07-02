package com.telco.members.tcos.controller;

import com.telco.members.tcos.model.Cashier;
import com.telco.members.tcos.model.LoanStatus;
import com.telco.members.tcos.model.leasureType;
import com.telco.members.tcos.service.CashierService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/members/cashbook/")
public class CashBook {

    @Autowired
    private final CashierService cashierService;

    @GetMapping("bondHistory")
    public String bondHistory(Model model){
        List<Cashier> type1 = cashierService.findByBLeasureTypeAndStatus(leasureType.BOND, LoanStatus.VERIFIED);
        model.addAttribute("bond", type1);
        return "Cashbook/bondHistory";
    }

    @GetMapping("voucherHistory")
    public String voucherHistory(Model model){
        List<Cashier> type1 = cashierService.findByBLeasureTypeAndStatus(leasureType.VOUCHER, LoanStatus.VERIFIED);
        model.addAttribute("voc", type1);
        return "Cashbook/voucherHistory";
    }

}
