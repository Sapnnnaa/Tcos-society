package com.telco.members.tcos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.telco.members.tcos.dto.*;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MemberReceiptService {

	@Autowired
	private usersRepository usersRepository;
	@Autowired
	private Bankrepository bankrepository;
	@Autowired
	private ReceiptRepository receiptRepository;
	@Autowired
	private RepoTempRid repoTempRid;
	@Autowired
	private AsyncMethod asyncMethod;
	@Autowired
	private AccountheadRepo accountheadRepo;
	@Autowired
	private MemberReceiptRepository memberReceiptRepository;

	
	public List<MemberReceipt> findAll(){
		return memberReceiptRepository.findAll();
	}
	
	public MemberReceipt save(MemberReceipt memberReceipt) {
		return memberReceiptRepository.save(memberReceipt);
	}

	public ReceiptDto convertToDto1(MemberReceipt mr){
		ReceiptDto rd = new ReceiptDto();
		rd.setName(mr.getName());
		rd.setCategory(mr.getCategory());
		rd.setPaymenttype(mr.getPaymenttype());
		rd.setUserId(mr.getUserId());
		rd.setTotalAmount(mr.getTotalAmount());
		rd.setRId(mr.getRId());
		rd.setCreatedDate(mr.getCreatedDate());
		rd.setUpdatedDate(mr.getUpdatedDate());
		rd.setApproveDate(mr.getApproveDate());
		return rd;
	}

	public List<MemberReceipt> update(List<MemberReceipt> memberReceipt) {
		// TODO Auto-generated method stub
		return memberReceiptRepository.saveAll(memberReceipt);
	}

    public List<ReceiptDto> findByStage(int stage) {
		return memberReceiptRepository.findByStage(stage)
				.stream().map(this::convertToDto1)
				.collect(Collectors.toList());
    }

	@Cacheable(value = "accountHeadCache")
	public List<AccountHeads> getAccountHeads() {
		return accountheadRepo.findAll();
	}

	public void deleteRecipits(String vId, String vSln) {
		receiptRepository.deleteByVIdAndVSln(vId, vSln);
	}

	public void deletevid(String vId) {
		memberReceiptRepository.deleteByrId(vId);
		receiptRepository.deleteByvId(vId);
	}

	public void addReceipts(String vId, String vSln, Double amount) {
		Receipt v1 = new Receipt();
		AccountHeads ah = accountheadRepo.findByslNo(vSln);
		String formattedVSln = ah.getSlNo() + "|" + ah.getAccountHead(); // Concatenating slNo and accountHead
		v1.setVId(vId);
		v1.setVSln(vSln);
		v1.setVouchertype(formattedVSln);
		v1.setAmount(amount);
		receiptRepository.save(v1);
	}

	public MemberReceiptDTO getdata(String rId) {
		MemberReceiptDTO dto = new MemberReceiptDTO();
		MemberReceipt mvs = memberReceiptRepository.findByrId(rId);
		List<Receipt> vs = receiptRepository.findByvId(rId);
		List<Receipt> voucherDTOList = new ArrayList<>();
		for (Receipt v : vs) {
			Receipt voucher = new Receipt();
			voucher.setVId(v.getVId());
			voucher.setVSln(v.getVSln());
			voucher.setVouchertype(v.getVouchertype());
			voucher.setAmount(v.getAmount());
			voucherDTOList.add(voucher);
		}
		dto.setMemberVoucher(voucherDTOList);
		dto.setRId(mvs.getRId());
		dto.setName(mvs.getName());
		dto.setUserId(mvs.getUserId());
		dto.setCategory(mvs.getCategory());
		dto.setAccountNum(mvs.getAccountNum());
		dto.setBname(mvs.getBname());
		dto.setStage(mvs.getStage());
		dto.setTotalAmount(mvs.getTotalAmount());
		dto.setPaymenttype(mvs.getPaymenttype());
		dto.setPaymentBank(mvs.getPaymentBank());
		dto.setChequeNo(mvs.getChequeNo());
		dto.setCreatedDate(mvs.getCreatedDate());
		dto.setPreparedBy(mvs.getPreparedBy());
		dto.setAccountNum(mvs.getAccountNum());
		dto.setDescription(mvs.getDescription());
		dto.setUpdatedDate(mvs.getUpdatedDate());
		dto.setCheckedBy(mvs.getCheckedBy());
		dto.setPassedBy(mvs.getPassedBy());
		dto.setApproveDate(mvs.getApproveDate());
		return dto;
	}

	public String getLatestRId() {
		String latestRid = repoTempRid.getLastId();
		String newNumberPart;
		if (latestRid == null || latestRid.isEmpty()) {
			newNumberPart = "0001";
		} else {
			String numberPart = latestRid.substring(1).trim();
			int number = Integer.parseInt(numberPart) + 1;
			newNumberPart = String.format("%0" + numberPart.length() + "d", number);
		}
		String newRid = "R" + newNumberPart;
		TempRid newEntry = new TempRid();
		newEntry.setTRid(newRid);
		repoTempRid.save(newEntry);
		return newRid;
	}

//	public synchronized String getLatestRId() {
//		Optional<String> latestVidOptional = memberReceiptRepository.findLatestrid();
//		return latestVidOptional.orElse("rec20250000");
//	}

	public void saveReceipt(MemberReceiptDTO mv) {
		MemberReceipt mv1 = memberReceiptRepository.findByrId(mv.getRId());
		mv1.setName(mv.getName());
		mv1.setAccountNum(mv.getAccountNum());
		mv1.setBname(mv.getBname());
		mv1.setTotalAmount(mv.getTotalAmount());
		mv1.setPaymenttype(mv.getPaymenttype());
		mv1.setPaymentBank(mv.getPaymentBank());
		mv1.setChequeNo(mv.getChequeNo());
		mv1.setCreatedDate(mv.getCreatedDate());
		mv1.setAccountNumber(mv.getAccountNumber());
		mv1.setPreparedBy(mv.getPreparedBy());
		mv1.setDescription(mv.getDescription());
		mv1.setStage(mv.getStage());
		memberReceiptRepository.save(mv1);
	}

	public void Receiptsave(Receipt mvs) {
		Receipt vc = new Receipt();
		vc.setVId(mvs.getVId());
		vc.setVSln(mvs.getVSln());
		vc.setVouchertype(mvs.getVouchertype());
		vc.setAmount(mvs.getAmount());
		receiptRepository.save(vc);
	}

	public void saveit(MemberReceiptDTO mv) {
		MemberReceipt mvs = new MemberReceipt();
		mvs.setRId(mv.getRId());
		mvs.setName(mv.getName());
		mvs.setUserId(mv.getUserId());
		mvs.setCategory(mv.getCategory());
		mvs.setAccountNum(mv.getAccountNum());
		mvs.setBname(mv.getBname());
		mvs.setTotalAmount(mv.getTotalAmount());
		mvs.setPaymenttype(mv.getPaymenttype());
		mvs.setPaymentBank(mv.getPaymentBank());
		mvs.setChequeNo(mv.getChequeNo());
		mvs.setCreatedDate(mv.getCreatedDate());
		mvs.setAccountNumber(mv.getAccountNumber());
		mvs.setPreparedBy(mv.getPreparedBy());
		mvs.setDescription(mv.getDescription());
		mvs.setStage(0);
		memberReceiptRepository.save(mvs);
		asyncMethod.getProxyRid(mv.getRId());
	}

	public MemberReceiptDTO findByUserId(String userId) {
		return convertDTO(userId);
	}

	private MemberReceiptDTO convertDTO(String userId) {
		MemberReceiptDTO dto = new MemberReceiptDTO();
		User user = usersRepository.findByUserId(userId);
	//	Banks banks = bankrepository.findByUserId(userId); bashishtha 03-04-2026
		dto.setUserId(userId);
		dto.setName(user.getName());
		//dto.setBname(banks.getBankName());
		//dto.setAccountNum(banks.getAccountNum());  // end bashishtha 03-04-2026
		return dto;
	}

	public void savemr(MemberReceiptDTO mr) {
		MemberReceipt mv1 = memberReceiptRepository.findByrId(mr.getRId());
		mv1.setUpdatedDate(mr.getUpdatedDate());
		mv1.setApproveDate(mr.getApproveDate());
		mv1.setCheckedBy(mr.getCheckedBy());
		mv1.setPassedBy(mr.getPassedBy());
		mv1.setStage(mr.getStage());
		memberReceiptRepository.save(mv1);
	}

	public void approvemr(MemberReceiptDTO receipt) {
		MemberReceipt mv1 = memberReceiptRepository.findByrId(receipt.getRId());
		mv1.setUpdatedDate(receipt.getUpdatedDate());
		mv1.setApproveDate(receipt.getApproveDate());
		mv1.setCheckedBy(receipt.getCheckedBy());
		mv1.setPassedBy(receipt.getPassedBy());
		mv1.setStage(receipt.getStage());
		memberReceiptRepository.save(mv1);
		System.out.println("Calling procedure with ReceiptId: " + mv1.getRId());
		asyncMethod.runProcessReceiptAsync(mv1.getRId());
	}

	public List<ReportDTO> findApprovedReport() {
		List<Object[]> rawData = memberReceiptRepository.findApprovedReportRaw();
		List<ReportDTO> reportList = new ArrayList<>();

		for (Object[] row : rawData) {
			ReportDTO dto = new ReportDTO();
			dto.setRId(String.valueOf(row[0]));
			dto.setCategory((String) row[1]);
			dto.setUserId((String) row[2]);
			dto.setName((String) row[3]);
			dto.setVouchertype((String) row[4]);
			dto.setAmount((Double) row[5]);
			dto.setApproveDate((Date) row[6]);

			reportList.add(dto);
		}

		return reportList;
	}


//    public List<VoucherDTO> findApprovedReport() {
//		int stage = 2;
//		List<MemberReceipt> lms = memberReceiptRepository.findByStage(stage);
//		List<VoucherDTO> ar = new ArrayList<>();
//		for(MemberReceipt mr : lms ){
//			List<Receipt> receit = receiptRepository.findByvId(mr.getRId());
//			for(Receipt rs : receit){
//				VoucherDTO dto = new VoucherDTO();
//				dto.setAmount(rs.getAmount());
//				dto.setVoucherApprovedDate(mr.getApproveDate());
//				dto.setVSln(rs.getVSln());
//				dto.setVouchertype(rs.getVouchertype());
//				dto.setVId(mr.getRId());
//				ar.add(dto);
//			}
//		}
//		return ar;
//    }
}
