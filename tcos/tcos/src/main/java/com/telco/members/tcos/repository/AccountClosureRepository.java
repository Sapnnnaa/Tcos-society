package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.AccountClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountClosureRepository extends JpaRepository<AccountClosure, Integer> {

}