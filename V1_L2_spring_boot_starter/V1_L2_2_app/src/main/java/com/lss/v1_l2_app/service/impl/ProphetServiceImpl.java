package com.lss.v1_l2_app.service.impl;

import com.lss.v1_l2_app.properties.ProfileConstants;
import com.lss.v1_l2_app.service.ProphetService;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConstants.SESSION_IS_COMING)
public class ProphetServiceImpl implements ProphetService {

    @Override
    public boolean approveCredit(String name) {
        // ThreadLocalRandom.current() - повертає екземпляр
        // ThreadLocalRandom, прив'язаний до поточного потоку.
        // Особливість: Вам не потрібно створювати об'єкт ThreadLocalRandom за допомогою new.
        // Ви завжди отримуєте екземпляр, який є унікальним для потоку, що його викликає.
        // .nextBoolean()
        // Це метод, який генерує і повертає наступне псевдовипадкове булеве значення.
        // Він з рівною ймовірністю поверне true або false.
        return !name.contains("Student") && ThreadLocalRandom.current().nextBoolean();
    }
}
