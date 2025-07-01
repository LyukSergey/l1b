package com.lss;

import com.lss.entity.Bank;
import com.lss.repository.MoneyRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {AbstractJpaTest.JpaConfigTest.class, SchemaInit.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public abstract class AbstractJpaTest {

    @EnableJpaRepositories(basePackageClasses = MoneyRepository.class)
    @EntityScan(basePackageClasses = Bank.class)
    public static class JpaConfigTest {

    }
}
