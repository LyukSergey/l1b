package com.lss.v1_l1_spring_boot_start.service;

public interface TransferMoneyService {

    long transfer(String name, Long amount, Long bankId);
}
