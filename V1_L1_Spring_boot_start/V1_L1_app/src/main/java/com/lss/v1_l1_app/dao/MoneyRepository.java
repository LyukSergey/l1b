package com.lss.v1_l1_app.dao;

import com.lss.v1_l1_spring_boot_start.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyRepository extends JpaRepository<Bank, Long> {

}
