package com.telco.members.tcos.service;

import com.telco.members.tcos.dto.ReceiptReportDTO;
import com.telco.members.tcos.dto.loanDTO;
import com.telco.members.tcos.dto.transactionHistoryDTO;
import com.telco.members.tcos.dto.SuretyloanDTO;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MisReportService {

    @Autowired
    private MemberReportRepository MemberReportRepository;

    @Autowired
    private loanrecoveryReportRepository loanrecoveryReportRepository;

    @Autowired
    private usersRepository usersRepository;

    @Autowired
    private suretiesReportRepository suretiesReportRepository;

    @Autowired
    private paymentsReportRepository paymentsReportRepository;

    @Autowired
    private CdShareReportRepository CdShareReportRepository;

    @Autowired
    private adviceReportRepository adviceReportRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PaymentAccountdetRepo paymentAccountdetRepo;

    @Autowired
    private AccountassignRepo accountassignRepo;
    // -----------------------------------------------
    // RECEIPT REPORT USING JDBC TEMPLATE
    // -----------------------------------------------
    public List<ReceiptReportDTO> getAllReceiptReports() {

        String sql = """
           SELECT
                mr.name AS memberName,
                mr.user_id AS userId,
                mr.r_id AS receiptId,
                mr.payment_bank AS paymentBank,
                mr.account_number AS accountNo,
                mr.cheque_no AS chequeNo,
                mr.paymenttype AS paymentType,
                mr.total_amount AS totalAmount,
                mr.total_amount AS paymentReceived,
                mr.category AS category,
                mr.created_date AS createdAt,           
             SUBSTRING(receipt.vouchertype,1,5) AS GLcode,
           -- SUBSTRING(receipt.vouchertype,7) AS GLname
             account_assign.account_head AS GLname
            FROM member_receipt  mr             
            INNER JOIN receipt ON receipt.v_id = mr.r_id
            INNER JOIN account_assign on SUBSTRING(receipt.vouchertype,1,5) = account_assign.sl_no
            WHERE mr.stage = 2
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ReceiptReportDTO dto = new ReceiptReportDTO();

            dto.setMemberName(rs.getString("memberName"));
            dto.setUserId(rs.getString("userId"));
            dto.setReceiptId(rs.getString("receiptId"));
            dto.setPaymentBank(rs.getString("paymentBank"));
            dto.setAccountNo(rs.getString("accountNo"));
            dto.setChequeNo(rs.getString("chequeNo"));

            String rawPaymentType = rs.getString("paymentType");
            dto.setPaymentType(rawPaymentType);

            try {
                dto.setPaymentTypeEnum(Paymenttype.valueOf(rawPaymentType));
            } catch (Exception e) {
                dto.setPaymentTypeEnum(Paymenttype.NONE);
            }

            dto.setTotalAmount(rs.getDouble("totalAmount"));
            dto.setPaymentReceived(rs.getDouble("paymentReceived"));
            dto.setCategory(rs.getString("category"));
            dto.setCreatedAt(rs.getTimestamp("createdAt"));
            dto.setGLcode(rs.getString("GLcode"));
            dto.setGLname(rs.getString("GLname"));

            return dto;
        });
    }
 ////
    public List<ReceiptReportDTO> getAllReceiptstatsReports() {

        String sql = """
           SELECT
                mr.name AS memberName,
                mr.user_id AS userId,
                mr.r_id AS receiptId,
                mr.payment_bank AS paymentBank,
                mr.account_number AS accountNo,
                mr.cheque_no AS chequeNo,
                mr.paymenttype AS paymentType,
                mr.total_amount AS totalAmount,
                mr.total_amount AS paymentReceived,
                mr.category AS category,
                mr.created_date AS createdAt,
             account_assign.account_head AS GLname,
             CASE WHEN mr.stage = 0 THEN 'Receipts made' WHEN mr.stage = 1 THEN 'Receipts posted' 
             WHEN mr.stage = 2 THEN 'Receipts cleared'  END AS GLcode
            FROM member_receipt  mr             
            INNER JOIN receipt ON receipt.v_id = mr.r_id
            INNER JOIN account_assign on SUBSTRING(receipt.vouchertype,1,5) = account_assign.sl_no
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ReceiptReportDTO dto = new ReceiptReportDTO();

            dto.setMemberName(rs.getString("memberName"));
            dto.setUserId(rs.getString("userId"));
            dto.setReceiptId(rs.getString("receiptId"));
            dto.setPaymentBank(rs.getString("paymentBank"));
            dto.setAccountNo(rs.getString("accountNo"));
            dto.setChequeNo(rs.getString("chequeNo"));       

            dto.setTotalAmount(rs.getDouble("totalAmount"));
            dto.setPaymentReceived(rs.getDouble("paymentReceived"));
            dto.setCategory(rs.getString("category"));
            dto.setCreatedAt(rs.getTimestamp("createdAt"));            
            dto.setGLname(rs.getString("GLname"));
            dto.setGLcode(rs.getString("GLcode"));

            return dto;
        });
    }

    // ------------------------------------------------
    // NAME MAPPING FOR LOANS
    // ------------------------------------------------
    public void attachNamesToLoans(List<Loan> loans) {
        for (Loan loan : loans) {
            String name = usersRepository.findNameByUserId(loan.getUserId());
            loan.setApprovedBy(name);
        }
    }

    // ------------------------------------------------
    // NAME MAPPING FOR SURETIES
    // ------------------------------------------------
    public void attachNamesToSureties(List<Surety> sureties) {
        for (Surety s : sureties) {
            String name = usersRepository.findNameByUserId(s.getUserId());
            s.setMemberName(name);
        }
    }
    //Advice
    public void attachNamesToLoanVoucher(List<LoanVoucher> vouchers) {
        for (LoanVoucher v : vouchers) {
            String name = usersRepository.findNameByUserId(v.getUserId());
            v.setMemberName(name);
        }
    }
    //CD & SHARE

    public String getCompanyName(int companyId) {
        try {
            String sql = "SELECT company_name FROM company WHERE company_id = ?";
            return jdbcTemplate.queryForObject(sql, String.class, companyId);
        } catch (Exception e) {
            return "N/A";
        }
    }
    public void attachNamesToCDShare(List<CDShareDeductions> list) {
        for (CDShareDeductions cd : list) {

            // MEMBER NAME
            String name = usersRepository.findNameByUserId(cd.getUserId());
            cd.setMemberName(name);

            // TICKET NO (same as userId)
            cd.setTicketNo(cd.getUserId());

            // COMPANY ID
            Integer companyId = usersRepository.findCompanyIdByUserId(cd.getUserId());

            // COMPANY NAME
            String companyName = "N/A";
            if (companyId != null) {
                companyName = getCompanyName(companyId);
            }
            cd.setCompanyName(companyName);
        }
    }






    // ------------------------------------------------
    // OTHER MIS REPORT METHODS
    // ------------------------------------------------
    public List<User> getAllMembership() {
        return MemberReportRepository.findAll();
    }

    public List<loanDTO> getAllLoanRecovery() {
       // return loanrecoveryReportRepository.findAll();
    	String sql = """
                 select   users.name AS approvedBy,mr.user_id AS userId,loan_amount AS loanAmount,
               CASE WHEN loan_type = 0 THEN 'PERSONAL' WHEN loan_type = 1 THEN 'COMMODITY' WHEN loan_type = 2 THEN 'MEMBERS' WHEN loan_type = 3 THEN 'EMPLOYEE' WHEN loan_type = 4 THEN 'VENDOR'
  WHEN loan_type = 5 THEN 'OTHER'END AS  loantypevalue,loan_apply_date AS loanApplyDate,loan_repaid AS loanRepaid,case  WHEN loan_type = 0 AND tr.category = 'PERSONAL' THEN tr.total_amount
        WHEN loan_type = 1 AND tr.category = 'COMMODITY' THEN tr.total_amount
        WHEN loan_type = 2 AND tr.category = 'MEMBERS' THEN tr.total_amount
        WHEN loan_type = 3 AND tr.category = 'EMPLOYEE' THEN tr.total_amount
         WHEN loan_type = 4 AND tr.category = 'VENDOR' THEN tr.total_amount
        ELSE 0
    END AS loandeposit
  ,loan_balance AS loanBalance,CASE WHEN loan_status = 0 THEN 'PENDING' WHEN loan_status = 1 THEN 'CHECKED' WHEN loan_status = 2 THEN 'VERIFIED' WHEN loan_status = 3 THEN 'APPROVED' WHEN loan_status = 4 THEN 'REJECTED'
  WHEN loan_status = 5 THEN 'PAID'  WHEN loan_status = 6 THEN 'FORWARD'  END  AS loanStatus

  FROM `loans` mr
  INNER join users on users.user_id = mr.user_id
  left join member_receipt tr on mr.user_id = tr.user_id
  group by mr.user_id,loan_type 
            """;
    	 return jdbcTemplate.query(sql, (rs, rowNum) -> {
    		 loanDTO dto = new loanDTO();

             dto.setName(rs.getString("approvedBy"));             
             dto.setUserId(rs.getString("userId"));
             dto.setLoanAmount(rs.getDouble("loanAmount")); 
           
            // dto.setLoantypevalue(rs.getString("loantypevalue"));
             
             String rawloanvalue = rs.getString("loantypevalue");             
             dto.setLoantypevalue(rawloanvalue);

             try {
                 dto.setLoantypevalueEnum(LoanType.valueOf(rawloanvalue));
             } catch (Exception e) {
                 dto.setLoantypevalueEnum(LoanType.OTHER);
             }
             
             dto.setLoanApplyDate(rs.getTimestamp("loanApplyDate"));
             
             dto.setLoanRepaid(rs.getDouble("loanRepaid"));
             dto.setLoandeposit(rs.getDouble("loandeposit"));
             dto.setLoanBalance(rs.getDouble("loanBalance"));
             
             String rawloanStatus = rs.getString("loanStatus");
             dto.setLoanStatusType(rawloanStatus);

             try {
                 dto.setLoanStatusTypeEnum(LoanStatus.valueOf(rawloanStatus));
             } catch (Exception e) {
                 dto.setLoanStatusTypeEnum(LoanStatus.PENDING);
             }
             
       
             return dto;
         });
    
    }

    public List<transactionHistoryDTO> getPaymentposted() {
        // return loanrecoveryReportRepository.findAll();
     	String sql = """
                  SELECT  bond_id as transid,account_assign.account_head,'MEMBERS' as category,users.name ,loan_voucher.user_id,'BOND' as transcationtype, CASE WHEN payment_type = 0 THEN 'NONE' WHEN payment_type = 1 THEN 'ADVICE' WHEN payment_type = 2 THEN 'NEFT' WHEN payment_type = 3 THEN 'CASH' WHEN payment_type = 4 THEN 'CHEQUE' WHEN payment_type = 5 THEN 'ONLINE' WHEN payment_type = 6 THEN 'Receipt' WHEN payment_type = 7 THEN 'Payment' END AS paymenttype,payment_bank,cheque_no,amount,advice_date as created_date,CASE WHEN payment_status =0 Then 'New Payment' WHEN payment_status =2 Then 'Payment Posted' WHEN payment_status =3 Then 'Payment Cleared' END AS payment_status FROM `loan_voucher` 
INNER join users on loan_voucher.user_id = users.user_id
INNER JOIN account_assign on loan_voucher.bsln = account_assign.sl_no 
 where   payment_status = 2
 
 UNION
 
 SELECT  member_voucher.v_id as transid,account_assign.account_head,category,name,member_voucher.user_id,'voucher' as TransactionType,CASE WHEN paymenttype = 0 THEN 'NONE' WHEN paymenttype = 1 THEN 'ADVICE' WHEN paymenttype = 2 THEN 'NEFT' WHEN paymenttype = 3 THEN 'CASH' WHEN paymenttype = 4 THEN 'CHEQUE' WHEN paymenttype = 5 THEN 'ONLINE' WHEN paymenttype = 6 THEN 'Receipt' WHEN paymenttype = 7 THEN 'Payment' END AS paymenttype,payment_bank,cheque_no,total_amount as amount,created_date,CASE WHEN payment_status =0 Then 'New Payment' WHEN payment_status =2 Then 'Payment Posted' WHEN payment_status =3 Then 'Payment Cleared' END AS payment_status FROM `member_voucher`
 INNER JOIN voucher on member_voucher.v_id = voucher.v_id
INNER JOIN account_assign on voucher.v_sln = account_assign.sl_no 
 where   payment_status = 2
             """;
     	 return jdbcTemplate.query(sql, (rs, rowNum) -> {
     		transactionHistoryDTO dto = new transactionHistoryDTO();

              dto.setTransactionId(rs.getString("transid"));             
              dto.setTransactionName(rs.getString("account_head"));
              dto.setCategory(rs.getString("category")); 
            
         
              
              dto.setName(rs.getString("name"));
              
              dto.setUserId(rs.getString("user_id"));
             dto.setDescription(rs.getString("transcationtype"));
             dto.setPaymenttypevalue(rs.getString("paymenttype"));
            
              dto.setPaymentBank(rs.getString("payment_bank"));          
              dto.setUniqueId(rs.getInt("cheque_no")); 
              dto.setAmount(rs.getDouble("amount"));            
           
              dto.setTransactionDate(rs.getTimestamp("created_date")); 
              dto.setPaymentstatusvalue(rs.getString("payment_status")); 
        
              return dto;
          });
     
     }
    public List<Surety> getAllSureties() {
        return suretiesReportRepository.findAll();
    }

    public List<transactionHistory> getAllPayments() {
        return paymentsReportRepository.findAll();
    }

    public List<LoanVoucher> getAllAdvice() {
        return adviceReportRepository.findAll();
    }


    public List<CDShareDeductions> getAllData() {
        return CdShareReportRepository.findAll();
    }
    public List<PaymentAccountdet> getpaymentaccountdata() {
        return paymentAccountdetRepo.getpaymentaccountdata();
    }
    
    public List<AccountAssign> getAccountAssign() {
		return accountassignRepo.findAll();
	}
    
    public List<SuretyloanDTO> getSuretyReports() {

        String sql = """
            SELECT users.name,loans.user_id,surety.g_name,surety.g_user_id,CASE WHEN loans.loan_type = 0 THEN 'PERSONAL' WHEN loans.loan_type = 1 THEN 'COMMODITY' WHEN loans.loan_type = 2 THEN 'MEMBERS' WHEN loans.loan_type = 3 THEN 'EMPLOYEE' WHEN loans.loan_type = 4 THEN 'VENDOR'
  WHEN loan_type = 5 THEN 'OTHER'END AS  loantypevalue  ,loans.id FROM `loans`
left join surety on loans.user_id = surety.user_id  
left join users on loans.user_id = users.user_id
WHERE surety.g_user_id IS NOT NULL
group by surety.g_user_id,loans.loan_type
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
        	SuretyloanDTO dto = new SuretyloanDTO();

            dto.setName(rs.getString("name"));
            dto.setUser_id(rs.getString("user_id"));
            dto.setG_name(rs.getString("g_name"));
            dto.setG_user_id(rs.getString("g_user_id"));
            dto.setLoantypevalue(rs.getString("loantypevalue"));        


            return dto;
        });
    }
}
