package com.huertohogar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Activa @CreatedDate y @LastModifiedDate
public class HuertoHogarApplication {
    public static void main(String[] args) {
        SpringApplication.run(HuertoHogarApplication.class, args);
    }
}