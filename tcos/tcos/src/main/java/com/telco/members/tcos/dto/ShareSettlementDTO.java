package com.telco.members.tcos.dto;

import lombok.Data;
import java.util.List;

@Data
public class ShareSettlementDTO {

    private double openingBalance;

    private List<ShareMonthDTO> months;

    private double closingBalance;

}