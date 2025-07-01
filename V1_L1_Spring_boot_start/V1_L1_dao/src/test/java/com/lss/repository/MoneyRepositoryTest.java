package com.lss.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.lss.AbstractJpaTest;
import com.lss.entity.Bank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MoneyRepositoryTest extends AbstractJpaTest {

    @Autowired
    private MoneyRepository moneyRepository;

    @Test
    void saveBankShouldReturnNewBank() {
        // Given
        Bank bank = new Bank();
        bank.setName("Test Bank");

        // When
        final Bank result = moneyRepository.save(bank);

        // Then
        assertThat(result).isNotNull();
    }

}