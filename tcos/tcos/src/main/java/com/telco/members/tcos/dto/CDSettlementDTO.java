package com.telco.members.tcos.dto;

import lombok.Data;
import java.util.List;

@Data
public class CDSettlementDTO {

    private double openingBalance;

    private List<CDMonthDTO> months;

    private double closingBalance;

    private double interest;

}