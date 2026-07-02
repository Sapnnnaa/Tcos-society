package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.re_Advice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReAdviceRepository extends JpaRepository<re_Advice, Integer> {


    Optional<re_Advice> findByAdviceId(int id);
}
