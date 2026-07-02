package com.telco.members.tcos.service;

import com.telco.members.tcos.dto.AccountDTO;
import com.telco.members.tcos.dto.UserregisterDTO;
import com.telco.members.tcos.dto.loanDTO;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.CompanyRepository;
import com.telco.members.tcos.repository.usersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountService {

    @Autowired
    private usersRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private User user;

    public List<AccountDTO> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getuserByUserId(String userId) {
        Optional<User> user= Optional.ofNullable(userRepository.findByUserId(userId));
        return user.map(this::convertToDTO).orElse(null);
    }

    public User getUser(String empId) {
        user = userRepository.findByEmpId(empId);
        return user;
    }

    @Transactional
    public void deleteUser(String empId) {
        this.userRepository.deleteByEmpId(empId);
    }

    public void updateUser(UserregisterDTO UserregisterDTO) {
        User user = userRepository.findByEmpId(UserregisterDTO.getEmpId());
        user.setEmail(UserregisterDTO.getEmail());
        user.setName(UserregisterDTO.getName());
        user.setPhoneNum(UserregisterDTO.getPhoneNum());
        String accstt = String.valueOf(UserregisterDTO.getAccountStatus());
        System.out.println("Account Status from DTO: " + accstt); // Log the account status passed
        user.setAccountStatus(UserregisterDTO.getAccountStatus());
        user.setPaymentMode(UserregisterDTO.getPaymentMode());
        user.setUtr(UserregisterDTO.getUtr());
        userRepository.save(user);
    }

    public AccountDTO convertToDTO(User user) {
        AccountDTO dto = new AccountDTO();
        dto.setUserId(user.getUserId());
        int companyid = (user.getCompanyId());
        Optional<Company> companyOptional = companyRepository.getCompanyById(companyid);

        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            dto.setCompany(company.getCompanyName());
//            dto.getCompany(company.getCompanyName(companyid));// Assuming getCompany() is a valid method
        } else {
            dto.setCompany("Not Declared"); // Default value or handle as per your requirement
        }
        dto.setName(user.getName());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setAccountSettlement(user.getAccountSettlement());
        return dto;
    }
}
