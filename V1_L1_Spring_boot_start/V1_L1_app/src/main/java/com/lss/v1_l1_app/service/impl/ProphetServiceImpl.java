package com.lss.v1_l1_app.service.impl;

import com.lss.v1_l1_app.config.ProfileConstants;
import com.lss.v1_l1_app.service.ProphetService;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConstants.WINTER_IS_COMING)
public class ProphetServiceImpl implements ProphetService {

    @Override
    public boolean willSurvive(String name) {
        return !name.contains("Stark") && ThreadLocalRandom.current().nextBoolean();
        // Якщо ім'я не містить "Stark", то випадковим чином визначаємо, чи виживе персонаж
        // Якщо ім'я містить "Stark", то персонаж не виживе, оскільки всі "Stark" приречені на загибель
    }
}
