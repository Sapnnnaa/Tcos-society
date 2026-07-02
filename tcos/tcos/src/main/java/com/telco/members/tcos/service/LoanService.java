package com.telco.members.tcos.service;

import com.telco.members.tcos.dto.LoanapproveDTO;
import com.telco.members.tcos.dto.loanDTO;
import com.telco.members.tcos.dto.reAdviceDTO;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.*;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

import java.util.Optional;

@Service
public class LoanService {

	@Autowired
	private loanRepository loanRepository;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;

	@Autowired
	private usersRepository usersRepository;

	@Autowired
	private AdviceRepository adviceRepository;

	@Autowired
	private ReAdviceRepository reAdviceRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private AsyncMethod asyncMethod;
	@Autowired
	private loanHistoryRepo loanhistoryrepo;

	@Autowired
	private CDShareDeductionRepository cdShareDeductionRepository;

	@Autowired
	private Bankrepository bankrepository;

	@Autowired
	private CashierRepo cashierRepo;

	@Value("${my.api.key}")
	private String apiKey;

	public List<loanDTO> getAllLoans() {
		return loanRepository.findByLoanStatus(LoanStatus.PENDING).stream().map(this::loanTable)
				.collect(Collectors.toList());
	}

	public List<loanDTO> getAllLoanstype1() {
		List<loanDTO> comList = loanRepository.findByLoanTypeAndLoanStatus(LoanType.COMMODITY, LoanStatus.APPROVED).stream()
				.map(this::loanTable).collect(Collectors.toList());
		comList.addAll(loanRepository.findByLoanTypeAndLoanStatus(LoanType.COMMODITY, LoanStatus.FORWARD).stream()
				.map(this::loanTable).collect(Collectors.toList()));
		comList.addAll(loanRepository.findByLoanTypeAndLoanStatus(LoanType.COMMODITY, LoanStatus.PAID).stream()
				.map(this::loanTable).collect(Collectors.toList()));
		return comList;
	}

	public List<loanDTO> getAllLoanstype2() {
		List<loanDTO> perList = loanRepository.findByLoanTypeAndLoanStatus(LoanType.PERSONAL, LoanStatus.APPROVED).stream()
				.map(this::loanTable).collect(Collectors.toList());
		perList.addAll(loanRepository.findByLoanTypeAndLoanStatus(LoanType.PERSONAL, LoanStatus.FORWARD).stream()
				.map(this::loanTable).collect(Collectors.toList()));
		perList.addAll(loanRepository.findByLoanTypeAndLoanStatus(LoanType.PERSONAL, LoanStatus.PAID).stream()
				.map(this::loanTable).collect(Collectors.toList()));
		return perList;
	}

	public loanDTO getLoanByLoanIdType1(int Id) {
		Loan loan = loanRepository.findByloanId(Id);
        String userId = loan.getUserId();
        if (loan.getLoanType() == LoanType.PERSONAL) {
            double loanbalance = loan.getLoanBalance();
			Optional<Loan> optionalLoan = loanRepository.findByLoanTypeAndLoanStatusAndUserId(LoanType.PERSONAL,
					LoanStatus.APPROVED, userId);
			if (optionalLoan.isPresent()) {
				Loan approvedLoan = optionalLoan.get();
				loan.setInterestDiff(approvedLoan.getInterestDiff());
				loan.setLoanApproveDate(approvedLoan.getLoanApproveDate());
				loan.setLoanBalance(approvedLoan.getLoanBalance() + loanbalance);
			}
		} else {
			loan.setLoanBalance(0);
            Optional<Loan> optionalLoan = loanRepository.findByLoanTypeAndLoanStatusAndUserId(LoanType.COMMODITY,
					LoanStatus.APPROVED, userId);
			if (optionalLoan.isPresent()) {
				Loan approvedLoan = optionalLoan.get();
				loan.setLoanApproveDate(approvedLoan.getLoanApproveDate());
				loan.setLoanBalance(approvedLoan.getLoanBalance());
			}
		}
		return convertToDTO(loan);
	}

	public loanDTO getLoanByloanId(int Id) {
		Optional<Loan> loan = loanRepository.findById(Id);
		return loan.map(this::convertToDTO).orElse(null);
	}

	public loanDTO getByloanId(int Id) {
		Optional<Loan> loan = loanRepository.findById(Id);
		return loan.map(this::convertToDTODetails).orElse(null);
	}

	public loanDTO getbyAdviceId(int Id) {
		LoanVoucher lv = adviceRepository.findByAdviceId(Id);
		return convertToDTOs(lv);
	}

	public reAdviceDTO getbyAdviceId1(int Id) {
		LoanVoucher lv = adviceRepository.findByAdviceId(Id);
		return convertDTOS(lv);
	}

	public void approveAdvice(String Id, paymentStatus PS) {
		LoanVoucher lv = adviceRepository.findByBondId(Id);
		if(PS == paymentStatus.Cleared){
			lv.setAdviceApprovedDate(Date.valueOf(LocalDate.now()));
			lv.setAdviceApproved(3);
			lv.setPaymentStatus(PS);
			adviceRepository.save(lv);
			approvedownload(convertFormDTOs(lv));
		}else {
			String status = "Cancelled";
			cashierRepo.updateBondCancelled(Id, status);
			lv.setPaymentStatus(PS);
			lv.setAdviceDownloaded(0);
			lv.setAdviceApproved(0);
			lv.setChequeNo(null);
			lv.setPostingDate(null);
			lv.setAdviceDate(Date.valueOf(LocalDate.now()));
			adviceRepository.save(lv);
		}
	}

	private reAdviceDTO convertFormDTOs(LoanVoucher loan) {
		reAdviceDTO dto = new reAdviceDTO();
		dto.setBondId(loan.getBondId());
		dto.setAdviceId(loan.getAdviceId());
		dto.setChequeNo(loan.getChequeNo());
		String useerId = loanRepository.findUserIdByLoanId(loan.getAdviceId());
		dto.setUserId(useerId);
		dto.setName(usersRepository.findNameByUserId(useerId));
		dto.setBname(bankrepository.getBankNameByuserId(useerId));
		dto.setBondType(loan.getBondType());
		dto.setGid(loan.getGid());
		dto.setAdviceApproved(loan.getAdviceApproved());
		dto.setPaymentStatus(loan.getPaymentStatus());
		dto.setPostingDate(loan.getPostingDate());
		dto.setLoanAmount(loan.getAmount());
		dto.setLoanType(loan.getLoanType());
		dto.setPaymenttype(loan.getPaymentType());
		dto.setPaymentBank(loan.getPaymentBank());
		dto.setAdviceApprovedDate(loan.getAdviceApprovedDate());
		dto.setUpdatedAt(loan.getUpdatedAt());
		dto.setAdviceDate(loan.getAdviceDate());
		return dto;
	}

	public List<loanDTO> findBydownloandednbankAndPreparedBy(int download, Paymenttype paymenttype, String preparedBy) {
		List<LoanVoucher> lvs = adviceRepository.findByPaymentTypeAndAdviceDownloadedAndPreparedBy(paymenttype, download, preparedBy);
		return lvs.stream().map(this::convertToDTOs).collect(Collectors.toList());
	}

	public List<loanDTO> findBydownloandednbankwithchequeAndpreparedBy(int download, Paymenttype paymenttype, String bankname, String preparedBy) {
		List<LoanVoucher> lvs = adviceRepository.findByPaymentTypeAndAdviceDownloadedAndPaymentBankAndPreparedBy(paymenttype, download, bankname, preparedBy);
		return lvs.stream().map(this::convertToDTOs).collect(Collectors.toList());
	}

//	public void saveChequeNo(int download, Paymenttype paymenttype, String chequeNo) {
//		List<LoanVoucher> lvs = adviceRepository.findByPaymentTypeAndAdviceDownloadedAndPreparedBy(paymenttype, download);
//		lvs.forEach(lv -> lv.setChequeNo(chequeNo));
//		adviceRepository.saveAll(lvs);
//	}

	public List<reAdviceDTO> findBydownloanded(int download, int approved) {
		List<LoanVoucher> lvs = adviceRepository.findByAdviceDownloadedAndAdviceApproved(download, approved);
		return lvs.stream().map(this::convertFormDTOs).collect(Collectors.toList());
	}

//    public List<loanDTO> findByApproved(int approved) {
//        List<LoanVoucher> lvs = adviceRepository.findByAdviceApproved(approved);
//        return lvs.stream()
//                .map(this::convertToDTOs)
//                .collect(Collectors.toList());
//    }

	private reAdviceDTO convertDTOS(LoanVoucher l1) {
		reAdviceDTO dto = new reAdviceDTO();
		dto.setAdviceId(l1.getAdviceId());
		dto.setUserId(l1.getUserId());
		Banks banksOptional = bankrepository.findByUserId(l1.getUserId());
		User userOptional = usersRepository.findByUserId(l1.getUserId());
		dto.setName(userOptional.getName());
		dto.setPhonenum(userOptional.getPhoneNum());
		dto.setCompanyName(companyRepository.getCompanyBycompanyId(userOptional.getCompanyId()));
		dto.setBondType(l1.getBondType());
		dto.setBondId(l1.getBondId());
		dto.setLoanType(l1.getLoanType());
		dto.setLoanAmount(l1.getAmount());
		dto.setBname(banksOptional.getBankName());
		dto.setAccountNum(banksOptional.getAccountNum());
		dto.setIfscode(banksOptional.getIfscCode());
		dto.setBranch(banksOptional.getBranchName());
		dto.setPayableAt(banksOptional.getPayableAt());
		dto.setPaymenttype(l1.getPaymentType());
		dto.setPaymentBank(l1.getPaymentBank());
		dto.setGid(l1.getGid());
		dto.setChequeNo(l1.getChequeNo());
		dto.setAdviceApprovedDate(l1.getAdviceApprovedDate());
		dto.setPreparedBy(l1.getPreparedBy());
		dto.setCheckedBy(l1.getCheckedBy());
		dto.setPassedBy(l1.getPassedBy());
		dto.setUpdatedAt(l1.getUpdatedAt());
		dto.setAdviceDate(l1.getAdviceDate());
		dto.setAdviceId(l1.getAdviceId());
		dto.setAdviceDownloaded(l1.getAdviceDownloaded());
		return dto;
	}


	private loanDTO convertToDTOs(LoanVoucher loan) {
		loanDTO dto = new loanDTO();
		dto.setId(loan.getAdviceId());
		Optional<Loan> loans = loanRepository.findById(loan.getAdviceId());
		if (loans.isPresent()) {
			Loan loan1 = loans.get();
			dto.setUserId(loan1.getUserId());
			Banks banksOptional = bankrepository.findByUserId(loan1.getUserId());
			User userOptional = usersRepository.findByUserId(loan1.getUserId());
			LoanVoucher voucherOptional = adviceRepository.findByUserId(loan1.getUserId());
			dto.setName(userOptional.getName());
			dto.setCompanyId(userOptional.getCompanyId());
			dto.setPhonenum(userOptional.getPhoneNum());
			dto.setAppliedLoan(loan1.getAppliedLoan());
			dto.setLoanApproveDate(loan1.getLoanApproveDate());
			dto.setAccountNum(banksOptional.getAccountNum());
			dto.setLoanBalance(loan1.getLoanBalance());
			dto.setBranch(banksOptional.getBranchName());
			dto.setBname(banksOptional.getBankName());
			dto.setPayableAt(banksOptional.getPayableAt());
			dto.setLoanReason(loan1.getLoanReason());
			dto.setPaymentBank(loan.getPaymentBank());
			dto.setChequeNo(loan.getChequeNo());
			dto.setBondId(loan.getBondId());
			dto.setBSln(loan.getBSln());
			dto.setBondType(loan.getBondType());
			dto.setAdviceApprovedDate(loan.getAdviceApprovedDate());
			dto.setIfscode(banksOptional.getIfscCode());
			dto.setLoanType(loan1.getLoanType());
			dto.setLoanAmount(loan1.getLoanAmount());
			dto.setCreatedAt(loan1.getLoanApplyDate());
			dto.setVerifiedBy(loan1.getVerifiedBy());
			dto.setPaymenttype(loan.getPaymentType());
			dto.setAdviceDate(loan.getAdviceDate());
			dto.setAdviceDownloaded(loan.getAdviceDownloaded());
			if (voucherOptional != null)
			{
			 dto.setAccountNumbers(voucherOptional.getAccountNumber());
			}
		}
		return dto;
	}

	private loanDTO loanTable(Loan loan) {
		loanDTO dto = new loanDTO();
		dto.setId(loan.getLoanId());
		dto.setUserId(loan.getUserId());
		User userOptional = usersRepository.findByUserId(loan.getUserId());
		dto.setName(userOptional.getName());
		dto.setCompanyName(companyRepository.getCompanyBycompanyId(userOptional.getCompanyId()));
		dto.setLoanStatus(loan.getLoanStatus());
		dto.setLoanType(loan.getLoanType());
		dto.setLoanAmount(loan.getLoanAmount());
		dto.setCreatedAt(loan.getLoanApplyDate());
		dto.setUpdatedAt(loan.getUpdatedAt());
		dto.setLoanApproveDate(loan.getLoanApproveDate());
		return dto;
	}

	private loanDTO convertToDTO(Loan loan) {
		loanDTO dto = new loanDTO();
		dto.setId(loan.getLoanId());
		dto.setUserId(loan.getUserId());
		Banks banksOptional = bankrepository.findByUserId(loan.getUserId());
		User userOptional = usersRepository.findByUserId(loan.getUserId());
		CDShareDeductions deductions = cdShareDeductionRepository.findByUserIds(loan.getUserId());
		dto.setDeductions(deductions.getTotalDeduction());
		dto.setName(userOptional.getName());
		dto.setCompanyName(companyRepository.getCompanyBycompanyId(userOptional.getCompanyId()));
		dto.setCompanyId(userOptional.getCompanyId());
		dto.setPhonenum(userOptional.getPhoneNum());
		dto.setCdDeduction(deductions.getCdDeductionTotal());
		dto.setShareDeduction(deductions.getShareDeductionTotal());
		dto.setLoanApproveDate(loan.getLoanApproveDate());
		dto.setAppliedLoan(loan.getAppliedLoan());
		dto.setAccountNum(banksOptional.getAccountNum());
		dto.setBranch(banksOptional.getBranchName());
		dto.setBname(banksOptional.getBankName());
		dto.setIfscode(banksOptional.getIfscCode());
		dto.setApplicationType(loan.getApplicationType());
		dto.setPayableAt(banksOptional.getPayableAt());
		dto.setInterestRate(loan.getIntrestRate());
		dto.setLoanRepaid(loan.getLoanRepaid());
		dto.setLoanReason(loan.getLoanReason());
		dto.setLoanBalance(loan.getLoanBalance());
		dto.setLoanStatus(loan.getLoanStatus());
		dto.setLoanType(loan.getLoanType());
		dto.setLoanAmount(loan.getLoanAmount());
		dto.setEndDate(loan.getEndDate());
		dto.setUpdatedAt(loan.getUpdatedAt());
		dto.setCreatedAt(loan.getLoanApplyDate());
		dto.setCheckedBy(loan.getUpdatedBy());
		dto.setVerifiedBy(loan.getVerifiedBy());
		return dto;
	}

	private loanDTO convertToDTODetails(Loan loan) {
		loanDTO dto = new loanDTO();
		dto.setId(loan.getLoanId());
		dto.setUserId(loan.getUserId());
		Banks banksOptional = bankrepository.findByUserId(loan.getUserId());
		User userOptional = usersRepository.findByUserId(loan.getUserId());
		dto.setCompanyName(companyRepository.getCompanyBycompanyId(userOptional.getCompanyId()));
		dto.setName(userOptional.getName());
		dto.setCompanyId(userOptional.getCompanyId());
		dto.setPhonenum(userOptional.getPhoneNum());
		dto.setAccountNum(banksOptional.getAccountNum());
		dto.setBranch(banksOptional.getBranchName());
		dto.setBname(banksOptional.getBankName());
		dto.setPayableAt(banksOptional.getPayableAt());
		dto.setIfscode(banksOptional.getIfscCode());
		dto.setInterestRate(loan.getIntrestRate());
		dto.setLoanBalance(loan.getLoanBalance());
		dto.setLoanType(loan.getLoanType());
		dto.setLoanAmount(loan.getLoanAmount());
		return dto;
	}

	public void updateLoans(loanDTO loandTO) {
		Loan loan = loanRepository.findByloanId(loandTO.getId());
		loan.setLoanBalance(loandTO.getLoanBalance());
		loan.setLoanApproveDate(loandTO.getLoanApproveDate());
		loan.setLoanStatus(loandTO.getLoanStatus());
		loan.setUpdatedBy(loandTO.getCheckedBy());
		loan.setVerifiedBy(loandTO.getVerifiedBy());
		loan.setUpdatedAt(Date.valueOf(LocalDate.now()));
		loanRepository.save(loan);
	}

//    @Transactional
//    public synchronized String getLatestBId() {
//        Optional<String> latestVidOptional = adviceRepository.findByLoanType(LoanType.PERSONAL);
//        return latestVidOptional.orElse("PB20250000");
//    }
//
//    @Transactional
//    public synchronized String getLatestBId_2() {
//        Optional<String> latestVidOptional = adviceRepository.findByLoanType(LoanType.COMMODITY);
//        return latestVidOptional.orElse("CB20250000");
//    }
//
////    public String incrementBId(String latestRid) {
////        // Extract numeric part and trim spaces
////        String numberPart = latestRid.substring(2).trim();
////        int number = Integer.parseInt(numberPart) + 1;
////        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
////        return "PB" + newNumberPart;
////    }
//
////    public String incrementBId_2(String latestRid) {
////        // Extract numeric part and trim spaces
////        String numberPart = latestRid.substring(2).trim();
////        int number = Integer.parseInt(numberPart) + 1;
////        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
////        return "CB" + newNumberPart;
////    }

	public void approvedLoans(loanDTO loandTO) {
		Loan loan = loanRepository.findByloanId(loandTO.getId());
		loanHistory lh = new loanHistory();
		LoanVoucher lv = new LoanVoucher();
		lh.setLoanId(loandTO.getId());
		lh.setUserId(loan.getUserId());
		lh.setLoanAmount(loan.getLoanAmount());
		lh.setLoantype(loan.getLoanType());
		lh.setLoanBalance((loan.getLoanBalance()) + (loan.getLoanAmount()));
		lh.setLoanDate(Date.valueOf(LocalDate.now()));
		loan.setLoanStatus(loandTO.getLoanStatus());
		loan.setLoanBalance((loan.getLoanBalance()) + (loan.getLoanAmount()));
		loan.setApprovedBy(loandTO.getApprovedBy());
		loan.setLoanApproveDate(Date.valueOf(LocalDate.now()));
		lv.setUserId(loandTO.getUserId());
		lv.setAdviceId(loan.getLoanId());
		lv.setLoanType(loan.getLoanType());
		lv.setLoanBalance((loan.getLoanBalance()) + (loan.getLoanAmount()));
		lv.setBondType("LOAN TO MEMBERS");
		lv.setBSln("L0015");
		lv.setAmount(loandTO.getLoanAmount());
		lv.setPaymentType(loandTO.getPaymenttype());
		lv.setAdviceDownloaded(0);
		loanhistoryrepo.save(lh);
		adviceRepository.save(lv);
		loanRepository.save(loan);
		asyncMethod.runCreateBondId(loan.getLoanId());
	}

	public void approvedLoansType2(loanDTO loandTO) {
		Loan loan = loanRepository.findByloanId(loandTO.getId());
		loanHistory lh = new loanHistory();
		lh.setLoanId(loandTO.getId());
		lh.setUserId(loan.getUserId());
		lh.setLoanAmount(loan.getLoanAmount());
		lh.setLoantype(loan.getLoanType());
		lh.setLoanBalance(loandTO.getLoanAmount());
		lh.setLoanDate(Date.valueOf(LocalDate.now()));
		loan.setLoanStatus(loandTO.getLoanStatus());
		loan.setApprovedBy(loandTO.getApprovedBy());
		loan.setLoanApproveDate(Date.valueOf(LocalDate.now()));
		LoanVoucher lv = new LoanVoucher();
		lv.setLoanType(loan.getLoanType());
		lv.setLoanBalance(loan.getLoanAmount());
		lv.setBondType("COMMODITY LOAN TO MEMBERS");
		lv.setBSln("C0003");
		lv.setAdviceId(loan.getLoanId());
		lv.setAmount(loandTO.getLoanAmount());
		lv.setPaymentType(loandTO.getPaymenttype());
		lv.setUserId(loan.getUserId());
		lv.setAdviceDownloaded(0);
		loanhistoryrepo.save(lh);
		adviceRepository.save(lv);
		loanRepository.save(loan);
		asyncMethod.runCreateBondId(loan.getLoanId());
	}

	public void updateadvice(loanDTO loandto) {
		LoanVoucher lv = adviceRepository.findByAdviceId(loandto.getId());
		lv.setUserId(loandto.getUserId());
		lv.setPreparedBy(loandto.getCheckedBy());
		lv.setPaymentType(loandto.getPaymenttype());
		lv.setPaymentBank(loandto.getPaymentBank());
		lv.setAccountNumber(loandto.getAccountNumbers());
		adviceRepository.save(lv);
	}

	public void reAdvice(loanDTO loandto) {
		Optional<re_Advice> reAdvice = reAdviceRepository.findByAdviceId(loandto.getId());
		if (reAdvice.isPresent()) {
			re_Advice re1 = reAdvice.get();
			int times = re1.getAdviceTimes();
			re1.setAdviceTimes(times + 1);
			re1.setAdviceDate2(loandto.getAdviceDate());
			reAdviceRepository.save(re1);
		} else {
			re_Advice re = new re_Advice();
			re.setAdviceId(loandto.getId());
			re.setAmount(loandto.getLoanAmount());
			re.setPaymentType(loandto.getPaymenttype());
			re.setUserId(loandto.getUserId());
			re.setAdviceDate(loandto.getAdviceDate());
			re.setAdviceTimes(1);
			reAdviceRepository.save(re);
		}
	}

	public void approvedownload(reAdviceDTO ls) {
		LoanVoucher lv = adviceRepository.findByAdviceId(ls.getAdviceId());
		Banks bs = bankrepository.findByUserId(lv.getUserId());
		// sending sms for loan credited
		String senderId = "TCOSMS";
		OkHttpClient client = new OkHttpClient();
		String last4 = bs.getAccountNum().substring(bs.getAccountNum().length() - 4);
		String message = "Dear Member,\r\n" + "Your Loan for Amount " + ls.getLoanAmount()
				+ " has been approved and credited to your Bank Account No. XXXX XXXX XXXX " + last4 + "\r\n" + "\r\n"
				+ "-Sigma e Solution Pvt. Ltd.";

		RequestBody formBody = new FormBody.Builder().add("module", "TRANS_SMS").add("apikey", apiKey)
				.add("to", ls.getPhonenum() + "").add("from", senderId).add("msg", message).build();
		Request request = new Request.Builder().url("https://2factor.in/API/R1/").post(formBody).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				System.out.println("loan approval sms successful");
			} else {
				System.out.println("Failed to sms OTP");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Calling procedure with bondId: " + lv.getBondId());
		asyncMethod.runProcessAdviceAsync(lv.getBondId());
	}

	public void checkAdvice(reAdviceDTO loandto) {
		LoanVoucher lv = adviceRepository.findByAdviceId(loandto.getAdviceId());
		lv.setUpdatedAt(loandto.getUpdatedAt());
		lv.setCheckedBy(loandto.getCheckedBy());
		lv.setAdviceApproved(loandto.getAdviceApproved());
		adviceRepository.save(lv);
	}

	public void cahiers(reAdviceDTO loandto) {
		LoanVoucher lv = adviceRepository.findByAdviceId(loandto.getAdviceId());
		lv.setUpdatedAt(loandto.getUpdatedAt());
		lv.setCheckedBy(loandto.getCheckedBy());
		lv.setAdviceApproved(loandto.getAdviceApproved());
		adviceRepository.save(lv);
		System.out.println("Calling cashier procedure with bondId: " + lv.getBondId());
		asyncMethod.runProcessCashierAsync(lv.getBondId());
	}

	public void updatedownload(reAdviceDTO loandto) {
		LoanVoucher lv = adviceRepository.findByAdviceId(loandto.getAdviceId());
		lv.setUpdatedAt(Date.valueOf(LocalDate.now()));
		lv.setAdviceDownloaded(loandto.getAdviceDownloaded());
		lv.setCheckedBy(loandto.getCheckedBy());
		adviceRepository.save(lv);
		System.out.println("Calling cancleAdvice 1 procedure with bondId: " + lv.getBondId());
		asyncMethod.runProcessCancleAdviceAsync(lv.getBondId());
	}

	public void updatepass(reAdviceDTO loandto) {
		LoanVoucher lv = adviceRepository.findByAdviceId(loandto.getAdviceId());
		lv.setUpdatedAt(Date.valueOf(LocalDate.now()));
		lv.setPassedBy(loandto.getPassedBy());
		lv.setAdviceApproved(loandto.getAdviceApproved());
		adviceRepository.save(lv);
		System.out.println("Calling cancleAdvice 2 procedure with bondId: " + lv.getBondId());
		asyncMethod.runProcessCancleAdviceAsync2(lv.getBondId());
	}

	public String generateNextGid() {
		String gid = adviceRepository.findLatestGid();

		if (gid == null || gid.isEmpty()) {
			return "B00001";
		}
		// Extract numeric part (assumes format like B00023)
		String numberPart = gid.substring(1); // Remove prefix 'B'
		int number = Integer.parseInt(numberPart);
		number++; // Increment
		// Build new gid with same prefix and 5-digit number
		return "B" + String.format("%05d", number);
	}

	public void updateAdviceDownload(List<loanDTO> memberVouchers) {
		String newGid = generateNextGid();
		for (loanDTO mv : memberVouchers) {
			mv.setGid(newGid);
			adviceRepository.updateAdviceDownload(mv.getChequeNo(), mv.getAdviceDate(), mv.getAdviceDownloaded(),
					mv.getId(), mv.getGid());
		}
	}

	public void updateAdviceNeft(List<loanDTO> memberVouchers) {
		String newGid = generateNextGid();
		for (loanDTO mv : memberVouchers) {
			mv.setGid(newGid);
			adviceRepository.updateAdviceNeft(mv.getAdviceDate(), mv.getAdviceDownloaded(), mv.getId(), mv.getGid());
		}
	}

//    public void updateadviceNeft(List<loanDTO> memberVouchers){
//        for(loanDTO mv : memberVouchers){
//            LoanVoucher lv = adviceRepository.findByAdviceId(mv.getId());
//            lv.setAdviceDate(mv.getAdviceDate());
//            lv.setAdviceDownloaded(mv.getAdviceDownloaded());
//            adviceRepository.save(lv);}
//    }

	public List<loanDTO> getLoanByloanStatus(LoanStatus loanStatus) {
		return loanRepository.findByLoanStatus(loanStatus).stream().map(this::loanTable).collect(Collectors.toList());
	}

	public List<reAdviceDTO> fetchALLAdvice() {
		return reAdviceRepository.findAll().stream().map(this::convertToDTOA).collect(Collectors.toList());
	}

	private reAdviceDTO convertToDTOA(re_Advice reAdvice) {
		reAdviceDTO re = new reAdviceDTO();
		re.setAdviceId(reAdvice.getAdviceId());
		re.setUserId(reAdvice.getUserId());
		Banks banksOptional = bankrepository.findByUserId(reAdvice.getUserId());
		User userOptional = usersRepository.findByUserId(reAdvice.getUserId());
		re.setPaymenttype(reAdvice.getPaymentType());
		re.setAdviceDate(reAdvice.getAdviceDate());
		re.setLoanAmount(reAdvice.getAmount());
		re.setAdviceDate2(reAdvice.getAdviceDate2());
		re.setTimes(reAdvice.getAdviceTimes());
		re.setName(userOptional.getName());
		re.setBname(banksOptional.getBankName());
		re.setAccountNum(banksOptional.getAccountNum());
		return re;
	}

	public List<Loan> byLoanStatus(LoanStatus loanStatus, int companyId) {
		return loanRepository.findByLoanStatusAndCompanyId(loanStatus, companyId);
	}

	public List<Loan> findForwarededLoan(String userId, LoanType loanType, LoanStatus loanStatus,
			java.util.Date startDate, java.util.Date endDate) {
		List<Loan> loans;
		try {
			loans = loanRepository.findByUserIdAndLoanTypeAndLoanStatusAndLoanApplyDateBetween(userId, loanType,
					loanStatus, startDate, endDate);
		} catch (Exception e) {
			// TODO: handle exception
			loans = null;
		}
		return loans;
	}

	public Loan save(Loan loan) {
		return loanRepository.save(loan);
	}

	public Loan findLatestLoan(String userId, LoanType loanType, LoanStatus loanStatus) {
		return loanRepository.findByUserIdAndLoanTypeAndLoanStatus(userId, loanType, loanStatus);
	}

//	public List<LoanapproveDTO> getAllNotApprovedLoan(String userIdsave) {
//		List<Object[]> l1 = loanRepository.findByUserIdandLoanTypeEqual(userIdsave);
//
//		return l1.stream().map(obj -> new LoanapproveDTO(
//				(String) obj[0],
//				(LoanStatus) obj[1],
//				(Date) obj[2],
//				(LoanType) (obj[3]),
//				((Number) obj[4]).doubleValue()
//		)).collect(Collectors.toList());
//	}
	public List<LoanapproveDTO> getAllNotApprovedLoan(String userIdsave) {
		List<Object[]> rows = loanRepository.findByUserIdandLoanTypeEqual(userIdsave);
		LoanStatus s1 = LoanStatus.APPROVED;
		LoanType t1;
		return rows.stream().map(obj -> {
			String userId = (String) obj[0];
			int statusValue = ((Number) obj[1]).intValue();
			LoanStatus status = LoanStatus.fromOrdinal(statusValue);
			Date applyDate = (Date) obj[2];
			int type = ((Number) obj[3]).intValue();
			LoanType loanType = LoanType.fromOrdinal(type);
			double amount = ((Number) obj[4]).doubleValue();
			return new LoanapproveDTO(userId, status, applyDate, loanType, amount);
		}).collect(Collectors.toList());
	}

	public int countLP() {
	return loanRepository.countLp();
	}

	public int countLloanp() {
		return loanRepository.countLP();
	}

	public int countAdvicep() {
		return adviceRepository.countAP();
	}

	public List<reAdviceDTO> findBydownloandedandapproved(int download, int approved, String paymentBank) {
		List<LoanVoucher> lvs = adviceRepository.findByAdviceDownloadedAndAdviceApprovedAndPaymentStatusAndPaymentBank(download, approved, paymentStatus.Posted, paymentBank);
		return lvs.stream().map(this::convertFormDTOs).collect(Collectors.toList());
	}

	public void loanEnd() {
		loanRepository.loanEnd();
	}
	public LoanInfo findLoanInfo() {
		// TODO Auto-generated method stub
		return loanInfoRepository.findById(1);
	}
	public LoanInfo updateLoanInfo(LoanInfo loanInfo) {
		// TODO Auto-generated method stub
		return loanInfoRepository.save(loanInfo);
	}
	
	public Double findByOutStandingLoan(String userId, LoanType loanType) {
		// TODO Auto-generated method stub

		Double osLoan = 0.0;
		List<Loan> lst = loanRepository
				.findByUserIdAndLoanTypeAndLoanBalanceGreaterThanOrderByLoanApproveDateDesc(userId, loanType, 0);
		if (lst == null)
			return 0.0;

		try {
			osLoan += lst.get(0).getLoanBalance();
		} catch (Exception e) {
			// TODO: handle exception
			osLoan=0.0;
		}
		

		return osLoan;
	}
	
	public int findInstalment(Date retirementDate, Date todaysDate) {
	    int maxInstalments = 50;
	    
	    // Add null check and debug logging
	    if (retirementDate == null) {
	        System.err.println("Retirement date is null");
	        return maxInstalments;
	    }
	    
	    if (todaysDate.compareTo(retirementDate) >= 0) {
	        System.err.println("Retirement date is in the past or today");
	        return maxInstalments;
	    }
	    
	    try {
	        LocalDate retirementLocalDate;
	        LocalDate todaysLocalDate;
	        
	        // Handle java.sql.Date (from @Temporal annotation)
	        if (retirementDate instanceof java.sql.Date) {
	            retirementLocalDate = ((java.sql.Date) retirementDate).toLocalDate();
	        } else {
	            // Handle java.util.Date
	            retirementLocalDate = retirementDate.toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDate();
	        }
	        
	        if (todaysDate instanceof java.sql.Date) {
	            todaysLocalDate = ((java.sql.Date) todaysDate).toLocalDate();
	        } else {
	            todaysLocalDate = todaysDate.toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDate();
	        }
	        
	        // Calculate months until retirement
	        long monthsUntilRetirement = ChronoUnit.MONTHS.between(todaysLocalDate, retirementLocalDate);
	        
	        // Ensure positive result and within limits
	        int availableInstalments = (int) Math.min(Math.max(monthsUntilRetirement, 1), maxInstalments);
	        
//	        System.out.println("Months until retirement: " + monthsUntilRetirement);
//	        System.out.println("Available instalments: " + availableInstalments);
	        
	        return availableInstalments;
	        
	    } catch (Exception e) {
	        System.err.println("Error calculating instalments: " + e.getMessage());
	        e.printStackTrace();
	        return maxInstalments;
	    }
	}
	public Loan findByLastLoanTypeAndStatus(String userId, LoanType loanType, LoanStatus loanStatus) {
		// TODO Auto-generated method stub
		Loan l = loanRepository.findTop1ByUserIdAndLoanTypeAndLoanStatusOrderByLoanApproveDateDesc(userId, loanType,
				loanStatus);
		return l;
	}
	public Date findByLastLoanApproveDateByUserId(String userId, LoanType loanType) {
		
		Loan record = loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
		java.util.Date utilDate = record.getLoanApproveDate();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());		
		
		return sqlDate;
		
	}
	
	public LoanStatus findLastLoanStatus(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		int ls = 0;
		if (loanType == LoanType.PERSONAL) {
			ls = loanRepository.countPendingLoans(userId, 0);
		} else {
			ls = loanRepository.countPendingLoans(userId, 1);
		}

		if (ls != 0)
			return LoanStatus.PENDING;
		else
			return LoanStatus.APPROVED;
	}
	public Loan findLastApproveLoan(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		return loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
	}
	
	
	public Double findByLastLoanOutStandingByUserId(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		Loan record;
		double amt;
		try {
			record = loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
			amt = record.getLoanBalance();
		} catch (Exception e) {
//			System.out.println("no loans found");
			amt = 0;
		}
		return amt;
	}
	
}
