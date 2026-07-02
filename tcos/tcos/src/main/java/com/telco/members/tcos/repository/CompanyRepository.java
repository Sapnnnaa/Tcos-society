package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Company;
import com.telco.members.tcos.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> getCompanyById(Integer companyId);
    Company findByCompanyId(int companyId);

    @Query(value = "SELECT company_name FROM company WHERE company_id = :companyId",nativeQuery = true)
    String getCompanyBycompanyId(@Param("companyId") int companyId);
}
