package com.lss.v1_l1_app.config;

import com.lss.repository.MoneyRepository;
import com.lss.entity.Bank;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = MoneyRepository.class)
@EntityScan(basePackageClasses = Bank.class)
public class JpaConfiguration {
}
