package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountassignRepo extends JpaRepository<AccountAssign,Integer>{
	AccountAssign findByslNo(String vSln);
}
