package com.lss.v1_l1_spring_boot_start.dao;

import com.lss.v1_l1_spring_boot_start.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyDao extends JpaRepository<Bank, Long> {

}
