package me.zilzu.mycoupon.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CouponIdGeneratorTest {

    CouponIdGenerator sut = new CouponIdGenerator();

    @Test
    @DisplayName("couponId")
    void test1() {
        String couponId = sut.generate();
        assertThat(couponId).hasSize(8);
    }

}