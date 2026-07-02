package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.AccountHeads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountheadRepo extends JpaRepository<AccountHeads,Integer> {

    AccountHeads findByslNo(String vSln);
}
