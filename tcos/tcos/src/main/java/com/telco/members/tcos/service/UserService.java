package com.telco.members.tcos.service;

import com.telco.members.tcos.dto.AccountDTO;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.Bankrepository;
import com.telco.members.tcos.repository.CompanyRepository;
import com.telco.members.tcos.repository.NomineeRepository;
import com.telco.members.tcos.repository.usersRepository;
import com.telco.members.tcos.repository.loanRepository;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.telco.members.tcos.dto.UserregisterDTO;
import com.telco.members.tcos.dto.loanDTO;

import java.time.LocalDate;


@Service
public class UserService {
    @Autowired
    private usersRepository userRepository;
    @Autowired
    private Bankrepository bankrepository;
    @Autowired
    private NomineeRepository nomineeRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private User user;

    @Autowired
    private loanRepository loanRepository;
	@Autowired
	private AsyncMethod asyncMethod;

    public List<UserregisterDTO> getUnactiveuser() {

        List<UserregisterDTO> users = userRepository
                .findByAccountStatusOrderByIdDesc(AccountStatus.getDefault())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        users.sort((u1, u2) -> {
            if ("REGISTERED".equals(u1.getRegistrationStatus())
                    && !"REGISTERED".equals(u2.getRegistrationStatus())) {
                return -1;
            }

            if (!"REGISTERED".equals(u1.getRegistrationStatus())
                    && "REGISTERED".equals(u2.getRegistrationStatus())) {
                return 1;
            }

            // Keep the existing order (Id Desc)
            return 0;
        });

        return users;
    }

    public List<UserregisterDTO> getAllUserclosure() {
        return userRepository.findByAccountStatusAndAccountSettlement(AccountStatus.ACTIVE, AccountSettlement.APPLIED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserregisterDTO> getAllUserclosed() {
        return userRepository.findByAccountStatusAndAccountSettlement(AccountStatus.CLOSED, AccountSettlement.APPROVED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<UserregisterDTO> getAllUserspecific(int companyId) {
        return userRepository.findByCompanyIdAndAccountStatus(companyId, AccountStatus.ACTIVE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public User getUser(String empId) {
        user = userRepository.findByEmpId(empId);
        return user;
    }
    public User getUsers(String userId) {
        user = userRepository.findByUserIdAndAccountStatus(userId, AccountStatus.ACTIVE);
        return user;
    }

    @Transactional
    public void deleteUser(String empId) {
        this.userRepository.deleteByEmpId(empId);
    }


    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return email.trim();
    }

    public void updateUser(UserregisterDTO userregisterDTO) {
        User user = userRepository.findByEmpId(userregisterDTO.getEmpId());
        String UserId = user.getUserId();
        Banks bank = bankrepository.findByUserId(UserId);
        Optional<Nominiees> nominiees = nomineeRepository.findByUserId(UserId);
        if(nominiees.isPresent()){
            Nominiees nominiees1 = nominiees.get();
        nominiees1.setNomineeName(userregisterDTO.getNomineeName());
        nominiees1.setRelation(userregisterDTO.getRelation());
        nominiees1.setAadharNum(userregisterDTO.getAadharNum());
        nominiees1.setMobileNum(userregisterDTO.getMobileNum());
        nomineeRepository.save(nominiees1);
        }
        bank.setBankName(userregisterDTO.getBname());
        bank.setBranchName(userregisterDTO.getBranch());
        bank.setAccountNum(userregisterDTO.getAccountNum());
        bank.setIfscCode(userregisterDTO.getIfscode());
        bank.setPayableAt(userregisterDTO.getPayableAt());
        user.setEmail(normalizeEmail(userregisterDTO.getEmail()));
        user.setName(userregisterDTO.getName());
        user.setPhoneNum(userregisterDTO.getPhoneNum());
        user.setAccountStatus(userregisterDTO.getAccountStatus());
        user.setPaymentMode(userregisterDTO.getPaymentMode());
        user.setApprovedAt(Date.valueOf(LocalDate.now()));
        user.setUtr(userregisterDTO.getUtr());
        user.setDateOfRetirement(userregisterDTO.getDor());
        user.setDob(userregisterDTO.getDob());
        user.setPanCard(userregisterDTO.getPanCard());
        user.setAadharNumber(userregisterDTO.getAadharNumber());
        user.setSerivcePeriod(userregisterDTO.getSerivcePeriod());
        user.setCreatedAt(userregisterDTO.getCreatedAt());
        user.setUserType(userregisterDTO.getUserType());
        user.setFathersName(userregisterDTO.getFathersName());
        user.setPermanentAddr(userregisterDTO.getPermanentAddr());
        user.setPermanentCity(userregisterDTO.getPermanentCity());
        user.setPermanentState(userregisterDTO.getPermanentState());
        user.setPermanentPinCode(userregisterDTO.getPermanentPinCode());
        user.setCurrentAddr(userregisterDTO.getCurrentAddr());
        user.setCurrentCity(userregisterDTO.getCurrentCity());
        user.setCurrentState(userregisterDTO.getCurrentState());
        user.setCurrentPinCode(userregisterDTO.getCurrentPinCode());
        bankrepository.save(bank);
        userRepository.save(user);

        // PERSONAL LOAN
        Loan personalLoan =
                loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                        UserId,
                        LoanType.PERSONAL
                );

        if(personalLoan != null){

            if(userregisterDTO.getCurrentPersonalLoan() != null){
                personalLoan.setLoanBalance(
                        userregisterDTO.getCurrentPersonalLoan()
                );
            }

            if(userregisterDTO.getPersonalLoanApprovalDate() != null){
                personalLoan.setLoanApproveDate(
                        userregisterDTO.getPersonalLoanApprovalDate()
                );
            }

            loanRepository.save(personalLoan);
        }


// COMMODITY LOAN
        Loan commodityLoan =
                loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                        UserId,
                        LoanType.COMMODITY
                );

        if(commodityLoan != null){

            if(userregisterDTO.getCurrentCommodityLoan() != null){
                commodityLoan.setLoanBalance(
                        userregisterDTO.getCurrentCommodityLoan()
                );
            }

            if(userregisterDTO.getCommodityLoanApprovalDate() != null){
                commodityLoan.setLoanApproveDate(
                        userregisterDTO.getCommodityLoanApprovalDate()
                );
            }

            loanRepository.save(commodityLoan);
        }
    }

    public void newUser(UserregisterDTO userregisterDTO) {
        Optional<User> users = userRepository.findByUserIDs(userregisterDTO.getUserId());
        if(users.isPresent()) {
            User user = users.get();
            Banks bank = new Banks();
            Nominiees nominiees1 = new Nominiees();
            nominiees1.setUserId(userregisterDTO.getUserId());
            nominiees1.setNomineeName(userregisterDTO.getNomineeName());
            nominiees1.setRelation(userregisterDTO.getRelation());
            nominiees1.setAadharNum(userregisterDTO.getAadharNum());
            nominiees1.setMobileNum(userregisterDTO.getMobileNum());
            bank.setUserId(userregisterDTO.getUserId());
            bank.setBankName(userregisterDTO.getBname());
            bank.setBranchName(userregisterDTO.getBranch());
            bank.setAccountNum(userregisterDTO.getAccountNum());
            bank.setIfscCode(userregisterDTO.getIfscode());
            bank.setPayableAt(userregisterDTO.getPayableAt());
            user.setEmail(normalizeEmail(userregisterDTO.getEmail()));
            user.setDob(userregisterDTO.getDob());
            user.setName(userregisterDTO.getName());
            user.setPhoneNum(userregisterDTO.getPhoneNum());
            user.setWhatsappNumber(userregisterDTO.getWhatsappNumber());
            user.setDateOfRetirement(userregisterDTO.getDor());
            user.setGender(userregisterDTO.getGender());
            user.setBcNumber(userregisterDTO.getBcNumber());
            user.setEmpId(userregisterDTO.getEmpId());
            user.setAccountStatus(userregisterDTO.getAccountStatus());
            user.setPaymentMode(userregisterDTO.getPaymentMode());
            user.setApprovedAt(Date.valueOf(LocalDate.now()));
            user.setUtr(userregisterDTO.getUtr());
            user.setCompanyId(userregisterDTO.getCompanyId());
            user.setPanCard(userregisterDTO.getPanCard());
            user.setAadharNumber(userregisterDTO.getAadharNumber());
            user.setSerivcePeriod(userregisterDTO.getSerivcePeriod());
            user.setPassword(userregisterDTO.getPassword());
            user.setUserType(userregisterDTO.getUserType());
            user.setFathersName(userregisterDTO.getFathersName());
            user.setPermanentAddr(userregisterDTO.getPermanentAddr());
            user.setPermanentCity(userregisterDTO.getPermanentCity());
            user.setPermanentState(userregisterDTO.getPermanentState());
            user.setPermanentPinCode(userregisterDTO.getPermanentPinCode());
            user.setCurrentAddr(userregisterDTO.getCurrentAddr());
            user.setCurrentCity(userregisterDTO.getCurrentCity());
            user.setCurrentState(userregisterDTO.getCurrentState());
            user.setCurrentPinCode(userregisterDTO.getCurrentPinCode());
            nomineeRepository.save(nominiees1);
            bankrepository.save(bank);
            userRepository.save(user);
        }
        else{
            User user = new User();
            Banks bank = new Banks();
            Nominiees nominiees1 = new Nominiees();
            nominiees1.setUserId(userregisterDTO.getUserId());
            nominiees1.setNomineeName(userregisterDTO.getNomineeName());
            nominiees1.setRelation(userregisterDTO.getRelation());
            nominiees1.setAadharNum(userregisterDTO.getAadharNum());
            nominiees1.setMobileNum(userregisterDTO.getMobileNum());
            bank.setUserId(userregisterDTO.getUserId());
            bank.setBankName(userregisterDTO.getBname());
            bank.setBranchName(userregisterDTO.getBranch());
            bank.setAccountNum(userregisterDTO.getAccountNum());
            bank.setIfscCode(userregisterDTO.getIfscode());
            bank.setPayableAt(userregisterDTO.getPayableAt());
            user.setUserId(userregisterDTO.getUserId());
            user.setEmail(normalizeEmail(userregisterDTO.getEmail()));
            user.setName(userregisterDTO.getName());
            user.setDob(userregisterDTO.getDob());
            user.setPhoneNum(userregisterDTO.getPhoneNum());
            user.setWhatsappNumber(userregisterDTO.getWhatsappNumber());
            user.setDateOfRetirement(userregisterDTO.getDor());
            user.setGender(userregisterDTO.getGender());
            user.setBcNumber(userregisterDTO.getBcNumber());
            user.setEmpId(userregisterDTO.getEmpId());
            user.setAccountStatus(userregisterDTO.getAccountStatus());
            user.setPaymentMode(userregisterDTO.getPaymentMode());
            user.setApprovedAt(Date.valueOf(LocalDate.now()));
            user.setUtr(userregisterDTO.getUtr());
            user.setCompanyId(userregisterDTO.getCompanyId());
            user.setPanCard(userregisterDTO.getPanCard());
            user.setAadharNumber(userregisterDTO.getAadharNumber());
            user.setSerivcePeriod(userregisterDTO.getSerivcePeriod());
            user.setPassword(userregisterDTO.getPassword());
            user.setUserType(userregisterDTO.getUserType());
            user.setFathersName(userregisterDTO.getFathersName());
            user.setPermanentAddr(userregisterDTO.getPermanentAddr());
            user.setPermanentCity(userregisterDTO.getPermanentCity());
            user.setPermanentState(userregisterDTO.getPermanentState());
            user.setPermanentPinCode(userregisterDTO.getPermanentPinCode());
            user.setCurrentAddr(userregisterDTO.getCurrentAddr());
            user.setCurrentCity(userregisterDTO.getCurrentCity());
            user.setCurrentState(userregisterDTO.getCurrentState());
            user.setCurrentPinCode(userregisterDTO.getCurrentPinCode());
            nomineeRepository.save(nominiees1);
            bankrepository.save(bank);
            userRepository.save(user);
        }
    }

    public UserregisterDTO convertToDTOs(User users) {
        UserregisterDTO dto = new UserregisterDTO();
        dto.setUserId(users.getUserId());
        Banks bank = bankrepository.findByUserId(users.getUserId());
        Optional<Nominiees> nominiees = nomineeRepository.findByUserId(users.getUserId());
        if(nominiees.isPresent()) {
            Nominiees nominiees1 = nominiees.get();
            dto.setNomineeName(nominiees1.getNomineeName());
            dto.setRelation(nominiees1.getRelation());
            dto.setAadharNum(nominiees1.getAadharNum());
            dto.setMobileNum(nominiees1.getMobileNum());
        }
        dto.setBname(bank.getBankName());
        dto.setBranch(bank.getBranchName());
        dto.setAccountNum(bank.getAccountNum());
        dto.setPayableAt(bank.getPayableAt());
        dto.setIfscode(bank.getIfscCode());
        dto.setDob(users.getDob());
        dto.setDor(users.getDateOfRetirement());
        dto.setApprovedate(users.getApprovedAt());
        dto.setCloseddate(users.getClosedAt());
        dto.setEmpId(users.getEmpId());
        dto.setCreatedAt(users.getCreatedAt());
        dto.setEmail(users.getEmail());
        dto.setName(users.getName());
        dto.setBcNumber(users.getBcNumber());
        dto.setGender(users.getGender());
        dto.setPanCard(users.getPanCard());
        dto.setAadharNumber(users.getAadharNumber());
        dto.setCompanyId(users.getCompanyId());
        dto.setCompanyName(companyRepository.getCompanyBycompanyId(users.getCompanyId()));
        dto.setUserType(users.getUserType());
        dto.setSerivcePeriod(users.getSerivcePeriod());
        dto.setWhatsappNumber(users.getWhatsappNumber());
        dto.setFathersName(users.getFathersName());
        dto.setPermanentAddr(users.getPermanentAddr());
        dto.setPermanentCity(users.getPermanentCity());
        dto.setPermanentState(users.getPermanentState());
        dto.setPermanentPinCode(users.getPermanentPinCode());
        dto.setCurrentAddr(users.getCurrentAddr());
        dto.setCurrentCity(users.getCurrentCity());
        dto.setCurrentState(users.getCurrentState());
        dto.setCurrentPinCode(users.getCurrentPinCode());
        dto.setPhoneNum(users.getPhoneNum());
        dto.setAccountStatus(users.getAccountStatus());
        dto.setAccountSettlement(users.getAccountSettlement());
        dto.setPaymentMode(users.getPaymentMode());
        dto.setUtr(users.getUtr());

        Loan personalLoan =
                loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                        users.getUserId(),
                        LoanType.PERSONAL
                );

        if(personalLoan != null){
            dto.setCurrentPersonalLoan(personalLoan.getLoanBalance());
            dto.setPersonalLoanApprovalDate(personalLoan.getLoanApproveDate());
        }


        Loan commodityLoan =
                loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                        users.getUserId(),
                        LoanType.COMMODITY
                );

        if(commodityLoan != null){
            dto.setCurrentCommodityLoan(commodityLoan.getLoanBalance());
            dto.setCommodityLoanApprovalDate(commodityLoan.getLoanApproveDate());
        }
        return dto;
    }

    public UserregisterDTO convertToDTO1(User user) {
        UserregisterDTO dto = new UserregisterDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setPhoneNum(user.getPhoneNum());
        return dto;
    }

    public UserregisterDTO convertToDTO(User user) {
        UserregisterDTO dto = new UserregisterDTO();
        dto.setUserId(user.getUserId());
        dto.setDob(user.getDob());
        dto.setDor(user.getDateOfRetirement());
        dto.setApprovedate(user.getApprovedAt());
        dto.setCloseddate(user.getClosedAt());
        dto.setEmpId(user.getEmpId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhoneNum(user.getPhoneNum());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setAccountSettlement(user.getAccountSettlement());
        dto.setPaymentMode(user.getPaymentMode());
        dto.setUtr(user.getUtr());

        if (user.getEmpId() != null && !user.getEmpId().trim().isEmpty()) {
            dto.setRegistrationStatus("REGISTERED");
        } else {
            dto.setRegistrationStatus("NOT REGISTERED");
        }
        return dto;
    }

    
    public User findByUserId(String userId) {
    	return userRepository.findByUserId(userId);
    }

    public Guarantor findByUserIds(String userId) {
        User user2 = userRepository.findByUserId(userId);
        Guarantor guarantor = new Guarantor();
        guarantor.setG_userId(user2.getUserId());
        guarantor.setG_name(user2.getName());
        guarantor.setPhone_num(user2.getPhoneNum());
        return guarantor;
    }

    @Cacheable(value = "userIdsCache")
    public List<String> findAllUserId(){
    	return userRepository.findAllUserId();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
	public User updateUser(User user) {
		return userRepository.save(user);
	}
	
	public User findByUserIdAndAccountStatus(String userId) {
		
		return userRepository.findByUserIdAndAccountStatus(userId, AccountStatus.PENDING);
	}
	
	public void updateRetirementdt(UserregisterDTO userregisterDTOS) {
		
		System.out.println("Updating existing user with ID: " + userregisterDTOS.getUserId());
		userRepository.updateRetirementdt(userregisterDTOS.getDob(), userregisterDTOS.getDor(), userregisterDTOS.getUserId());
	}
	public void Insertorupdatebanks(UserregisterDTO userregisterDTOS)
	{
		System.out.println("Updating existing user banks: " + userregisterDTOS.getUserId());
		asyncMethod.callProcessbanks(userregisterDTOS.getUserId().toUpperCase(), userregisterDTOS.getAccountNum().toUpperCase(), userregisterDTOS.getBname().toUpperCase(), userregisterDTOS.getBranch().toUpperCase(), userregisterDTOS.getIfscode().toUpperCase(), userregisterDTOS.getPayableAt().toUpperCase());
	}
	
}

