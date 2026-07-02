package com.telco.members.tcos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.model.Company;
import com.telco.members.tcos.repository.CompanyRepository;


@Service
public class CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;

	public List<Company> getAllCompany() {
		return companyRepository.findAll();
	}

	public Company findbyCompanyId(int id) {
		// TODO Auto-generated method stub
		return companyRepository.findByCompanyId(id);
	}
	
}
