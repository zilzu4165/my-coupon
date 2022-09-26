package me.zilzu.mycoupon.application.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class CouponIdGenerator {

    private static final String CAPITAL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC = "0123456789";

    // "Z4OV52SU"
    // 26 * 36^7 = 2조개
    public String generate() {
        String prefix = RandomStringUtils.random(1, CAPITAL_ALPHABET);

        String body = RandomStringUtils.random(7, CAPITAL_ALPHABET + NUMERIC);

        return prefix + body;
    }
}
