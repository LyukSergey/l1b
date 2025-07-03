package com.lss.v1_l2_app.repository;

import com.lss.v1_l2_app.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyRepository extends JpaRepository<Bank, String> {

}
