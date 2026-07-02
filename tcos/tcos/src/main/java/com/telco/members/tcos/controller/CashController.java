package com.telco.members.tcos.controller;

import com.telco.members.tcos.dto.UserregisterDTO;
import com.telco.members.tcos.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.telco.members.tcos.model.Cashier;
import com.telco.members.tcos.model.LoanStatus;
import com.telco.members.tcos.model.leasureType;
import com.telco.members.tcos.service.CashierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping ("/members/cashier")
public class CashController {

    @Autowired
    private CashierService cashierService;

    @GetMapping("/bond")
    public String bondSec(Model model){
        List<Cashier> type1 = cashierService.findByBLeasureTypeAndStatus(leasureType.BOND, LoanStatus.PENDING);
        model.addAttribute("bond", type1);
        return "Bankbook/cashBond";
    }

    @GetMapping("/bond/cancelled")
    @ResponseBody
    public List<Cashier> bondCan() {
        return cashierService.findByBLeasureTypeAndStatus(leasureType.BOND, LoanStatus.REJECTED);
    }

    @GetMapping("/bond/{Id}")
    private ResponseEntity<String> searchUserIdB(@PathVariable int Id){
        cashierService.updatebond(Id);
        return ResponseEntity.ok("Bond updated successfully");
    }

    @GetMapping("/voc")
    public String VocSec(Model model){
        List<Cashier> type1 = cashierService.findByBLeasureTypeAndStatus(leasureType.VOUCHER, LoanStatus.PENDING);
        model.addAttribute("voc", type1);
        return "Bankbook/cashVoucher";
    }

    @GetMapping("/voc/cancelled")
    @ResponseBody
    public List<Cashier> VocCan(){
        return cashierService.findByVLeasureTypeAndStatus(leasureType.VOUCHER, LoanStatus.REJECTED);
    }

    @GetMapping("/voc/{Id}")
    private ResponseEntity<String> searchUserIdV(@PathVariable int Id){
        cashierService.updatevoucher(Id);
        return ResponseEntity.ok("Voucher updated successfully");
    }

}
