package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Tcosbanks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TcosbankRepo extends JpaRepository<Tcosbanks, Integer> {
    List<Tcosbanks> findAll();
}
