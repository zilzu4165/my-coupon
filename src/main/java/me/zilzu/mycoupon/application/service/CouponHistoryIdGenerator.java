package me.zilzu.mycoupon.application.service;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class CouponHistoryIdGenerator {
    public String generate() {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
