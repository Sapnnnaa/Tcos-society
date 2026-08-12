package com.telco.members.tcos.service;

import com.telco.members.tcos.model.AccountClosure;
import com.telco.members.tcos.repository.AccountClosureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountClosureService {

    @Autowired
    private AccountClosureRepository accountClosureRepository;

    public AccountClosure save(AccountClosure accountClosure) {
        return accountClosureRepository.save(accountClosure);
    }

}