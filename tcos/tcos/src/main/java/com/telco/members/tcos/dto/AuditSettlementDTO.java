package com.telco.members.tcos.dto;

import java.util.List;

import com.telco.members.tcos.model.AuditDetSettlement;
import com.telco.members.tcos.model.AuditDetSettlement1;
import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.model.User;
import com.telco.members.tcos.model.loanHistory;

import lombok.Data;

@Data
public class AuditSettlementDTO {
	List<AuditDetSettlement> auditdetsettlement;
	List<AuditDetSettlement1> auditdetsettlement1;
}
