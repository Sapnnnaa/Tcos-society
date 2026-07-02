package com.telco.members.tcos.service;

import java.time.LocalDate;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberVoucherService {
    @Autowired
	private MemberVoucherRepository memberVoucherRepository;
	@Autowired
	private usersRepository usersRepository;
	@Autowired
	private Bankrepository bankrepository;
	@Autowired
	private RepoTempVid repoTempVid;
	@Autowired
	private AsyncMethod asyncMethod;
	@Autowired
	private VocherRespository vocherRespository;
	@Autowired
	private AccountheadRepo accountheadRepo;


	public MemberVoucherDTO findByUserId(String userId) {
		return convertDTO(userId);
	}

	private MemberVoucherDTO convertDTO(String userId){
		MemberVoucherDTO dto = new MemberVoucherDTO();
		User user = usersRepository.findByUserId(userId);
		Banks banks = bankrepository.findByUserId(userId);
		dto.setUserId(userId);
		dto.setName(user.getName());
		dto.setBankName(banks.getBankName());
		dto.setAccountNum(banks.getAccountNum());
		dto.setBranchName(banks.getBranchName());
		dto.setIfscCode(banks.getIfscCode());
		dto.setPayableAt(banks.getPayableAt());
		return dto;
	}

	public List<MemberVoucher> findAll(){
		return memberVoucherRepository.findAll();
	}

	public MemberVoucher save(MemberVoucher memberVoucher) {
		return memberVoucherRepository.save(memberVoucher);
	}

	public MemberVoucher findById(int id) {
		return memberVoucherRepository.findById(id);
	}

	public String generateNextGid() {
		String gid = memberVoucherRepository.findLatestGid();
		if (gid == null || gid.isEmpty()) {
			return "V00001";
		}
		String numberPart = gid.substring(1);
		int number = Integer.parseInt(numberPart);
		number++;
		return "V" + String.format("%05d", number);
	}

	public void update(List<MemberVoucher> memberVoucherss) {
		String newGid = generateNextGid();
		for (MemberVoucher mv : memberVoucherss) {
			mv.setGid(newGid);
			memberVoucherRepository.updateVoucherDownloadedByVId(
					mv.getVoucherDownloaded(),
					mv.getUpdatedDate(),
					mv.getVId(),
					mv.getGid()
			);
		}
	}

	//12
	public void updateAdvice(List<MemberVoucher> memberVoucherss) {
		String newGid = generateNextGid();
		for (MemberVoucher mv : memberVoucherss) {
			mv.setGid(newGid);
			memberVoucherRepository.updateVoucherDownloadedByVIdCheque(
					mv.getChequeNo(),
					mv.getVoucherDownloaded(),
					mv.getUpdatedDate(),
					mv.getVId(),
					mv.getGid()
			);
		}
	}

	@Cacheable(value = "accountHeadCache")
	public List<AccountHeads> getAccountHeads() {
		return accountheadRepo.findAll();
	}

    public List<MemberVoucher> findByDownloaded(int download, int approved) {
		return memberVoucherRepository.findByVoucherDownloadedAndVoucherApproved(download,approved);
	}

	public List<TableDto> tableView(int download, int approved) {
		return memberVoucherRepository.findByVoucherDownloadedAndVoucherApproved(download,approved)
				.stream()
				.map(this::table_convert)
				.collect(Collectors.toList());
	}

	public List<TableDto> bankView(int download, int approved, String selectedBank) {
		return memberVoucherRepository.findByVoucherDownloadedAndVoucherApprovedAndPaymentStatusAndPaymentBank(download, approved, paymentStatus.Posted, selectedBank)
				.stream()
				.map(this::table_convert)
				.collect(Collectors.toList());
	}

	private TableDto table_convert(MemberVoucher mv) {
		TableDto dto = new TableDto();
		dto.setVId(mv.getVId());
		dto.setGid(mv.getGid());
		dto.setName(mv.getName());
		dto.setUserId(mv.getUserId());
		dto.setCategory(mv.getCategory());
		dto.setBankName(mv.getBankName());
		dto.setAccountNum(mv.getAccountNum());
		dto.setTotalAmount(mv.getTotalAmount());
		dto.setChequeNo(mv.getChequeNo());
		dto.setPaymentStatus(mv.getPaymentStatus());
		dto.setPostingDate(mv.getPostingDate());
		dto.setPaymenttype(mv.getPaymenttype());
		dto.setPaymentBank(mv.getPaymentBank());
		dto.setCreatedDate(mv.getCreatedDate());
		dto.setUpdatedDate(mv.getUpdatedDate());
		dto.setVoucherApprovedDate(mv.getVoucherApprovedDate());
		return dto;
	}

	private printDto convertToDTOs(MemberVoucher mv) {
		printDto dto = new printDto();
		dto.setAccountNum(mv.getAccountNum());
		dto.setName(mv.getName());
		dto.setVId(mv.getVId());
		dto.setUserId(mv.getUserId());
		dto.setCategory(mv.getCategory());
		dto.setAmount(mv.getTotalAmount());
		dto.setBranchName(mv.getBranchName());
		dto.setBankName(mv.getBankName());
		dto.setChequeNo(mv.getChequeNo());
		dto.setIfscCode(mv.getIfscCode());
		dto.setPayableAt(mv.getPayableAt());
		return dto;
	}

	public String getLatestVId() {
		String latestVid = repoTempVid.getLastId();
		String newNumberPart;
		if (latestVid == null || latestVid.isEmpty()) {
			newNumberPart = "0001";
		} else {
			String numberPart = latestVid.substring(1).trim();
			int number = Integer.parseInt(numberPart) + 1;
			newNumberPart = String.format("%0" + numberPart.length() + "d", number);
		}
		String newVid = "V" + newNumberPart;
		TempVid newEntry = new TempVid();
		newEntry.setTVid(newVid);
		repoTempVid.save(newEntry);
		return newVid;
	}

    public List<MemberVoucher> findByregenrated(int regenrated) {
		return memberVoucherRepository.findByRegenerated(regenrated);
    }

    public void saveit(MemberVoucherDTO mv) {
		MemberVoucher mvs = new MemberVoucher();
		mvs.setVId(mv.getVId());
		mvs.setName(mv.getName());
		mvs.setUserId(mv.getUserId());
		mvs.setCategory(mv.getCategory());
		mvs.setAccountNum(mv.getAccountNum());
		mvs.setBankName(mv.getBankName());
		mvs.setBranchName(mv.getBranchName());
		mvs.setIfscCode(mv.getIfscCode());
		mvs.setPayableAt(mv.getPayableAt());
		mvs.setTotalAmount(mv.getTotalAmount());
		mvs.setPaymenttype(mv.getPaymenttype());
		mvs.setPaymentBank(mv.getPaymentBank());
		mvs.setChequeNo(mv.getChequeNo());
		mvs.setCreatedDate(mv.getCreatedDate());
		mvs.setPaymentAccountNum(mv.getPaymentAccountNum());
		mvs.setPreparedBy(mv.getPreparedBy());
		mvs.setDescription(mv.getDescription());
		mvs.setVoucherDownloaded(0);
		mvs.setVoucherApproved(0);
		memberVoucherRepository.save(mvs);
		asyncMethod.getProxyVid(mv.getVId());
	}

	public void savemv(MemberVoucherDTO mv) {
		MemberVoucher mv1 = memberVoucherRepository.findByvId(mv.getVId());
		mv1.setName(mv.getName());
		mv1.setAccountNum(mv.getAccountNum());
		mv1.setBankName(mv.getBankName());
		mv1.setBranchName(mv.getBranchName());
		mv1.setIfscCode(mv.getIfscCode());
		mv1.setPayableAt(mv.getPayableAt());
		mv1.setTotalAmount(mv.getTotalAmount());
		mv1.setPaymenttype(mv.getPaymenttype());
		mv1.setPaymentBank(mv.getPaymentBank());
		mv1.setChequeNo(mv.getChequeNo());
		mv1.setCreatedDate(mv.getCreatedDate());
		mv1.setPaymentAccountNum(mv.getPaymentAccountNum());
		mv1.setPreparedBy(mv.getPreparedBy());
		mv1.setDescription(mv.getDescription());
		mv1.setVoucherDownloaded(0);
		mv1.setVoucherApproved(0);
		memberVoucherRepository.save(mv1);
	}

	public void checkmv(MemberVoucherDTO mv) {
		MemberVoucher mv1 = memberVoucherRepository.findByvId(mv.getVId());
		mv1.setUpdatedDate(mv.getUpdatedDate());
		mv1.setCheckedBy(mv.getCheckedBy());
//		mv1.setRegenerated(mv1.getRegenerated() == 1 ? 1 : mv.getRegenerated());
//		mv1.setGeneratedTimes(Math.max(mv1.getGeneratedTimes(), mv.getGeneratedTimes()));
		mv1.setVoucherDownloaded(mv.getVoucherDownloaded());
		mv1.setVoucherApproved(mv.getVoucherApproved());
		memberVoucherRepository.save(mv1);
	}

	public void passmv(MemberVoucherDTO mv) {
		MemberVoucher mv1 = memberVoucherRepository.findByvId(mv.getVId());
		mv1.setUpdatedDate(mv.getUpdatedDate());
		mv1.setPassedBy(mv.getPassedBy());
		mv1.setVoucherApproved(mv.getVoucherApproved());
		memberVoucherRepository.save(mv1);
	}

	public void rollbackmv(MemberVoucher mv1) {
		memberVoucherRepository.save(mv1);
	}

	public void bankmv(MemberVoucher mvs) {
		memberVoucherRepository.save(mvs);
		System.out.println("Calling procedure with VoucherId: " + mvs.getVId());
		asyncMethod.runProcessVoucherAsync(mvs.getVId());
	}


	public void savevoucher(Voucher mvs) {
		Voucher vc = new Voucher();
		vc.setVId(mvs.getVId());
		vc.setVSln(mvs.getVSln());
		vc.setVouchertype(mvs.getVouchertype());
		vc.setAmount(mvs.getAmount());
		vocherRespository.save(vc);
	}

	public MemberVoucherDTO getdata(String vId) {
		MemberVoucherDTO dto = new MemberVoucherDTO();
		MemberVoucher mvs = memberVoucherRepository.findByvId(vId);
		List<Voucher> vs = vocherRespository.findByvId(vId);
		List<Voucher> voucherDTOList = new ArrayList<>();
		for (Voucher v : vs) {
			Voucher voucher = new Voucher();
			voucher.setVId(v.getVId());
			voucher.setVSln(v.getVSln());
			voucher.setVouchertype(v.getVouchertype());
			voucher.setAmount(v.getAmount());
			voucherDTOList.add(voucher);
		}
		dto.setMemberVoucher(voucherDTOList);
		dto.setVId(mvs.getVId());
		dto.setName(mvs.getName());
		dto.setUserId(mvs.getUserId());
		dto.setCategory(mvs.getCategory());
		dto.setAccountNum(mvs.getAccountNum());
		dto.setBankName(mvs.getBankName());
		dto.setBranchName(mvs.getBranchName());
		dto.setIfscCode(mvs.getIfscCode());
		dto.setPayableAt(mvs.getPayableAt());
		dto.setPaymentStatus(mvs.getPaymentStatus());
		dto.setTotalAmount(mvs.getTotalAmount());
		dto.setPaymenttype(mvs.getPaymenttype());
		dto.setPaymentBank(mvs.getPaymentBank());
		dto.setChequeNo(mvs.getChequeNo());
		dto.setCreatedDate(mvs.getCreatedDate());
		dto.setPreparedBy(mvs.getPreparedBy());
		dto.setPaymentAccountNum(mvs.getPaymentAccountNum());
		dto.setDescription(mvs.getDescription());
		dto.setVoucherDownloaded(mvs.getVoucherDownloaded());
		dto.setVoucherApproved(mvs.getVoucherApproved());
		dto.setUpdatedDate(mvs.getUpdatedDate());
		dto.setCheckedBy(mvs.getCheckedBy());
		dto.setPassedBy(mvs.getPassedBy());
		dto.setGeneratedTimes(mvs.getGeneratedTimes());
		dto.setVoucherApprovedDate(mvs.getVoucherApprovedDate());
		return dto;
	}

    public void deletevid(String vId) {
		memberVoucherRepository.deleteByvId(vId);
		vocherRespository.deleteByvId(vId);
    }

	public void addVouchers(String vId, String vSln, double amount) {
		Voucher v1 = new Voucher();
		AccountHeads ah = accountheadRepo.findByslNo(vSln);
		String formattedVSln = ah.getSlNo() + "|" + ah.getAccountHead(); // Concatenating slNo and accountHead
		v1.setVId(vId);
		v1.setVSln(vSln);
		v1.setVouchertype(formattedVSln);
		v1.setAmount(amount);
		vocherRespository.save(v1);
	}

	@Transactional
	public void deleteVouchers(String vId, String vSln) {
		vocherRespository.deleteByVIdAndVSln(vId, vSln);
	}

    public List<MemberVoucher> findByPaymentTypeAndPreparedBy(Paymenttype paymenttype, String bankname, int download, String preparedBy) {
		return memberVoucherRepository.findByPaymenttypeAndVoucherDownloadedAndPaymentBankAndPreparedBy(paymenttype,download, bankname, preparedBy);
    }

	public List<MemberVoucher> findByCheque(Paymenttype paymenttype, int download, String preparedBy) {
		return memberVoucherRepository.findByPaymenttypeAndVoucherDownloadedAndPreparedBy(paymenttype, download, preparedBy);
	}

	public int findGenerated(String vId) {
		return memberVoucherRepository.findBygeneratedtimes(vId);
	}

	public void rolbackGid(String vid) {
		asyncMethod.runProcessCancelVoc(vid);
	}

	public void rolbackGid2(String vid) {
		asyncMethod.runProcessCancelVoc2(vid);
	}

	public void updateByGid(String vid) {
		asyncMethod.runProcessChasierVoc(vid);
	}

	public List<ReportDTO> findApprovedReport() {
			List<Object[]> rawData = memberVoucherRepository.findApprovedReportRaw();
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



//	public List<VoucherDTO> findApprovedReport() {
//		int stage = 2;
//		List<MemberVoucher> lms = memberVoucherRepository.findByVoucherApproved(stage);
//		List<VoucherDTO> ar = new ArrayList<>();
//		for(MemberVoucher mr : lms ){
//			List<Voucher> receit = vocherRespository.findByvId(mr.getVId());
//			for(Voucher rs : receit){
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
//	}
}
