package com.techshop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.techshop.common.entity"})
public class TechShopBackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(TechShopBackEndApplication.class, args);
    }

}
