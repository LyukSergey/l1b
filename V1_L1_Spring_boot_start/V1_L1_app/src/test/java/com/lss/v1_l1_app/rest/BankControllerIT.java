package com.lss.v1_l1_app.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lss.entity.Bank;
import com.lss.repository.MoneyRepository;
import com.lss.v1_l1_app.V1L1AppApplication;
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
@SpringBootTest(classes = {V1L1AppApplication.class,}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MoneyRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void creditShouldReturnSuccess() throws Exception {
        // Given
        final Bank bank = Bank.builder()
                .name("IronBank")
                .totalAmount(1000L)
                .build();
        repository.save(bank);

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