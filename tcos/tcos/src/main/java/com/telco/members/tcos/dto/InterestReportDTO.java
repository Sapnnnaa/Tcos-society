package com.telco.members.tcos.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InterestReportDTO {

    private String userId;
    private String name;
    private double loanTakenComm;private double loanTakenCommUpload;
    private double loanTakenNormal;private double loanTakenNormalUpload;
    private double loanInstalmentComm;private double loanInstalmentCommUpload;
    private double loanInstalmentNormal;private double loanInstalmentNormalUpload;
    private double loanInterestComm;private double loanInterestCommUpload;
    private double loanInterestNormal;private double loanInterestNormalUpload;
    private double cd;private double cdUpload;
    private double share;private double shareUpload;

}