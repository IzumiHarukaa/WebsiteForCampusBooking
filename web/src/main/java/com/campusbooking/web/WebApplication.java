package com.campusbooking.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// By default, @SpringBootApplication scans for components in its own package 
// and all sub-packages. Since all our components are in sub-packages of 
// 'com.campusbooking.web', we don't need to specify them manually.
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.campusbooking.web.repository")
@EntityScan(basePackages = {"com.campusbooking.web.model", "com.campusbooking.web.actor"})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}

