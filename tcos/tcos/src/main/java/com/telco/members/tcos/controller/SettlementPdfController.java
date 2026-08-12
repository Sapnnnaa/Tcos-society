package com.telco.members.tcos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telco.members.tcos.dto.SettlementDTO;
import com.telco.members.tcos.service.PdfService;
import com.telco.members.tcos.service.SettlementService;

import jakarta.servlet.http.HttpServletResponse;

import org.thymeleaf.context.Context;

@Controller
public class SettlementPdfController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/settlement/pdf")
    public void downloadSettlementPdf(
            @RequestParam String userId,
            @RequestParam String financialYear,
            HttpServletResponse response) throws Exception {

        SettlementDTO settlement =
                settlementService.getSettlement(userId, financialYear);

        Context context = new Context();

        context.setVariable("settlement", settlement);
        context.setVariable("financialYear", financialYear);
        context.setVariable("printDate", new java.util.Date());

        context.setVariable("memberId", settlement.getMemberId());
        context.setVariable("memberName", settlement.getMemberName());
        context.setVariable("company", settlement.getCompany());

        byte[] pdf = pdfService.generatePdf(
                "reports/settlement",
                context);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=settlement.pdf");

        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @GetMapping("/settlement/summary-pdf")
    public void downloadSummaryPdf(
            @RequestParam String userId,
            @RequestParam String financialYear,
            HttpServletResponse response) throws Exception {

        SettlementDTO settlement =
                settlementService.getSettlement(userId, financialYear);

        Context context = new Context();

        context.setVariable("settlement", settlement);

        context.setVariable("memberId", settlement.getMemberId());
        context.setVariable("memberName", settlement.getMemberName());
        context.setVariable("company", settlement.getCompany());

        context.setVariable("financialYear", financialYear);
        context.setVariable("printDate", new java.util.Date());

        byte[] pdf = pdfService.generatePdf(
                "reports/summary-settlement",
                context);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=SettlementSummary.pdf");

        response.getOutputStream().write(pdf);
    }
}