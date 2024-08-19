package com.techshop.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {"com.techshop.common.entity"})
@SpringBootApplication
public class TechShopFrontEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechShopFrontEndApplication.class, args);
    }

}
