package com.telco.members.tcos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.telco.members.tcos.model.transactionHistory;

public interface transactionRepo extends JpaRepository<transactionHistory, Integer> {
}
