package com.telco.members.tcos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telco.members.tcos.repository.AuditDetRepo;
import com.telco.members.tcos.repository.AuditDetRepo1;
import com.telco.members.tcos.model.*;
@Service
public class AuditDetService {
	 @Autowired
	 private AuditDetRepo auditdetrepo;	
	 @Autowired
	 private AuditDetRepo1 auditdetrepo1;	
	 public List<AuditDetSettlement> getAuditDetsdata(String p_userId,String p_audityear) {
	       return auditdetrepo.getAuditDetdata(p_userId,p_audityear);
	   }
	
	 public List<AuditDetSettlement1> getAuditDetsdata1(String p_userId,String p_audityear) {
	        return auditdetrepo1.getAuditDetnormdata(p_userId,p_audityear);
	    }
	 
	 public AuditDetSettlement1 getAuditsSummarydata(String p_userId,String p_audityear)
	 {
		 return auditdetrepo1.getAuditsummarydata(p_userId,p_audityear);
	 }
	 public AuditDetSettlement getAuditsummarys(String p_userId,String p_audityear) {
	       return auditdetrepo.getAuditsummary(p_userId,p_audityear);
	   }

}
