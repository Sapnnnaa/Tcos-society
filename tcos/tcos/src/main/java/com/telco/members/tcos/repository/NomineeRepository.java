package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.Nominiees;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NomineeRepository extends JpaRepository<Nominiees, Integer> {

    Optional<Nominiees> findByUserId(String userId);
}
