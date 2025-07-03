package com.lss.v1_l2_app.listener;

import com.lss.ContextRefreshedEventListener;
import com.lss.RavenProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MyContextRefreshedEventListener extends ContextRefreshedEventListener {

    public MyContextRefreshedEventListener(RavenProperties ravenProperties) {
        super(ravenProperties);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("event = " + event);
    }
}
