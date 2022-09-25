package me.zilzu.mycoupon.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponHistoryIdGeneratorTest {

    CouponHistoryIdGenerator sut = new CouponHistoryIdGenerator();

    @Test
    @DisplayName("CouponHistoryId")
    void test1() {
        String couponHistoryId = sut.generate();
        assertThat(couponHistoryId).hasSize(20);
    }
}
