package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Banktranstally;
import com.telco.members.tcos.model.LoanInfo;
import com.telco.members.tcos.model.PaymentAccountdet;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentAccountdetRepo  extends JpaRepository<PaymentAccountdet, Integer>{
	@Query(value = "SELECT banks.id,banks.user_id ,banks.account_num ,banks.bank_name ,banks.branch_name ,banks.ifsc_code ,banks.payable_at ,transaction_history.amount\r\n"
			+ ",transaction_history.transaction_date,CASE WHEN transaction_history.leasuretype = 0 THEN 'Payment' WHEN transaction_history.leasuretype = 1 THEN 'Receipt' WHEN transaction_history.leasuretype = 2 THEN 'Payment' WHEN transaction_history.leasuretype = 3 THEN 'OTHER' END AS leasuretype,transaction_history.transaction_id  transactionid,transaction_history.transaction_name transactionname,transaction_history.category\r\n"
			+ "FROM banks \r\n"
			+ " INNER JOIN transaction_history\r\n"
			+ "ON banks.user_id = transaction_history.user_id\r\n"
			+ "where  transaction_history.name = 'Bank Record' order by transaction_history.transaction_date desc", nativeQuery = true)
	List<PaymentAccountdet> getpaymentaccountdata();
}
