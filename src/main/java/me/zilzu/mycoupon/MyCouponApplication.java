package me.zilzu.mycoupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCouponApplication.class, args);
    }

}
