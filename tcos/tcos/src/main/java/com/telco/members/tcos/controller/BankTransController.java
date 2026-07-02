package com.telco.members.tcos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.telco.members.tcos.model.Banktranstally;
import com.telco.members.tcos.service.BanktranstallyService;
import com.telco.members.tcos.service.tallydownloadService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;

@RestController
//@Controller
public class BankTransController {
    @Autowired
    private BanktranstallyService banktranstallyService;

    @Autowired
    private tallydownloadService tallydownloadsService;

    @GetMapping("/api/banktranstally")
    public ResponseEntity<List<Banktranstally>> intForTh(
            @RequestParam("transdt") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime transdt) {

        List<Banktranstally> transList = banktranstallyService.getBanktranssdata(transdt);
        System.out.println(transdt);
        return ResponseEntity.ok(transList);

    }
    @PostMapping("/api/banktranstallydownload")
    public ResponseEntity<String> inserttallydown(@RequestParam("download_date") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime download_date) {
        tallydownloadsService.getbanktranstallydata(download_date);
        return ResponseEntity.ok("Tally data save successfully");
    }

}
