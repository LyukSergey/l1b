package com.lss.v1_l1_spring_boot_start;

import com.lss.v1_l1_spring_boot_start.config.properties.ProphetProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication // ✅ Магічна анотація!
@EnableConfigurationProperties(ProphetProperties.class) // ⚙️ Дозволяє використовувати кастомні властивості
public class V1L1SpringBootStartApplication {

    public static void main(String[] args) {
        // Запускає Spring Boot додаток
        SpringApplication.run(V1L1SpringBootStartApplication.class, args);
    }

}
