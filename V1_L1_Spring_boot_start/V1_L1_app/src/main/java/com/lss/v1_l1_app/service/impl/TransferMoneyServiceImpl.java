package com.lss.v1_l1_app.service.impl;

import com.lss.repository.MoneyRepository;
import com.lss.entity.Bank;
import com.lss.v1_l1_app.exception.ResourceNotFoundException;
import com.lss.v1_l1_app.service.ProphetService;
import com.lss.v1_l1_app.service.TransferMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferMoneyServiceImpl implements TransferMoneyService {

    private final ProphetService prophetService;
    private final MoneyRepository moneyDao;

    public long transfer(String name, Long amount, Long bankId) {
        final boolean survive = prophetService.willSurvive(name);
        final Bank bank = moneyDao.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Bank with id %s not found", bankId)));
        final double bankBalance = bank.getTotalAmount();
        if (survive && bankBalance >= amount) {
            return 100;
        }
        return 0;
    }

}
