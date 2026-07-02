package com.telco.members.tcos.controller;

import com.telco.members.tcos.dto.BankDTO;
import com.telco.members.tcos.model.bankBalance;
import com.telco.members.tcos.model.tcosBalance;
import com.telco.members.tcos.model.transactionHistory;
import com.telco.members.tcos.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/members/bankbook")
public class BankBook {

    @Autowired
    private BankService bankService;

//    private String formatCurrency(double value) {
//        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("en", "IN")); // Indian grouping
//        formatter.setMinimumFractionDigits(2);
//        formatter.setMaximumFractionDigits(2);
//        return formatter.format(value);
//    }

    @GetMapping("/history")
    public String bankHistory(Model model){
        List<bankBalance> bankBook = bankService.findAllData();
        List<Double> balanceList = bankService.findAllBalance();
        model.addAttribute("sbiBalance", balanceList.get(0));
        model.addAttribute("cbiBalance", balanceList.get(1));
        model.addAttribute("jscbBalance", balanceList.get(2));
        model.addAttribute("bankbook", bankBook);
        return "Bankbook/bankrecord";
    }

    @GetMapping("/transactionHistory")
    public String transactionHistory(Model model){
        List<transactionHistory> bankBook = bankService.getAllData();
        model.addAttribute("bankbook", bankBook);
        return "Bankbook/transactions";
    }

}
