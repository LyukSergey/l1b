package com.lss.v1_l2_app.service.impl;

import com.lss.v1_l2_app.properties.ProfileConstants;
import com.lss.v1_l2_app.properties.ProphetProperties;
import com.lss.v1_l2_app.service.ProphetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConstants.WINTER_IS_HERE)
@RequiredArgsConstructor
public class WhiteListBackendProphetServiceImpl implements ProphetService {

    private final ProphetProperties prophetProperties;

    @Override
    public boolean willSurvive(String name) {
        return prophetProperties.getWhoReturnDebts().contains(name);
    }
}
