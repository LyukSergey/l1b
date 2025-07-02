package com.lss.v1_l2_app.service;

import com.lss.v1_l2_app.properties.ProfileConstants;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConstants.WINTER_IS_COMING)
public class ProphetServiceImpl implements ProphetService {

    @Override
    public boolean willSurvive(String name) {
        return !name.contains("Stark") && ThreadLocalRandom.current().nextBoolean();
    }
}
