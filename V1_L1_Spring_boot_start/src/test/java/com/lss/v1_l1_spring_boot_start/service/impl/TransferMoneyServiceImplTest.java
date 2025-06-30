package com.lss.v1_l1_spring_boot_start.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lss.v1_l1_spring_boot_start.dao.MoneyDao;
import com.lss.v1_l1_spring_boot_start.entity.Bank;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferMoneyServiceImplTest {

    @InjectMocks
    private TransferMoneyServiceImpl transferMoneyService;
    @Mock
    private ProphetServiceImpl prophetService;
    @Mock
    private MoneyDao moneyDao;

    @Test
    void transfer() {
        // Given
        final String name = "John Snow";
        final Long amount = 100L;
        final Long bankId = 1L;
        final Bank bank = Bank.builder()
                .totalAmount(100)
                .build();

        when(prophetService.willSurvive(name)).thenReturn(true);
        when(moneyDao.findById(bankId)).thenReturn(Optional.of(bank));

        // When
        final long result = transferMoneyService.transfer(name, amount, bankId);

        // Then
        assertThat(result).isEqualTo(100L);
        verify(prophetService).willSurvive(name);
        verify(moneyDao).findById(bankId);
    }

}