package com.lss.v1_l2_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferMoneyServiceImpl {

    private final ProphetService prophetService;

    public long transfer(String name, Long amount) {
        final boolean survive = prophetService.willSurvive(name);
        if (survive) {
            return 100;
        }
        return 0;
    }

}
