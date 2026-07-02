package com.telco.members.tcos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.members.tcos.model.DueRecovery;

public interface DueRecoveryRepository extends JpaRepository<DueRecovery, Integer> {

}