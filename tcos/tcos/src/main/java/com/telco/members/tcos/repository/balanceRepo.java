package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.bankBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface balanceRepo extends JpaRepository<bankBalance, Integer> {

}
