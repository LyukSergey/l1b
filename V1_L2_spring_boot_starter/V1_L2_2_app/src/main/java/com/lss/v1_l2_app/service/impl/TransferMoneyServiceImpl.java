package com.lss.v1_l2_app.service.impl;

import com.lss.v1_l2_app.service.ProphetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferMoneyServiceImpl {

    private final ProphetService prophetService;

    public long transfer(String name, Long amount) {
        final boolean survive = prophetService.approveCredit(name);
        if (survive) {
            return 100;
        }
        return 0;
    }

}
