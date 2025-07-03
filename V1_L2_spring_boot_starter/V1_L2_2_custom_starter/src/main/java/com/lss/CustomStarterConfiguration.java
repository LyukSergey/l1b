package com.lss;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RavenProperties.class)
public class CustomStarterConfiguration {

    @Bean
    @ConditionOnProduction
//    @ConditionalOnProperty("raven.where")
    @ConditionalOnMissingBean
    @ConditionalOnRaven
    public ContextRefreshedEventListener ravenListener(RavenProperties ravenProperties) {
        return new ContextRefreshedEventListener(ravenProperties);
    }
}
