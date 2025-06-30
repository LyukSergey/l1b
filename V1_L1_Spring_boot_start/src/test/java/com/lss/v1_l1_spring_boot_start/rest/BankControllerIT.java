package com.lss.v1_l1_spring_boot_start.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lss.v1_l1_spring_boot_start.V1L1SpringBootStartApplication;
import com.lss.v1_l1_spring_boot_start.dao.MoneyDao;
import com.lss.v1_l1_spring_boot_start.entity.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {V1L1SpringBootStartApplication.class,}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankControllerIT {

    // Тут будуть тести для IronBankController
    // Використовуємо MockMvc для тестування контролерів
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MoneyDao moneyDao;

    @BeforeEach
    void setUp() {
        moneyDao.deleteAll();
    }

    @Test
    void credit() throws Exception {
        // Given
        final Bank bank = Bank.builder()
                .name("IronBank")
                .totalAmount(1000L)
                .build();
        moneyDao.save(bank);

        // When & Then
        mockMvc.perform(get("/credit")
                        .param("name", "John")
                        .param("amount", "100")
                        .param("bankId", bank.getId().toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void creditShouldThrowResourceNotFoundExceptionWhenBankIsNotFound() throws Exception {
        // Given, When, Then
        mockMvc.perform(get("/credit")
                        .param("name", "John")
                        .param("amount", "100")
                        .param("bankId", "1000"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.message").value("Bank with id 1000 not found"));
    }

}