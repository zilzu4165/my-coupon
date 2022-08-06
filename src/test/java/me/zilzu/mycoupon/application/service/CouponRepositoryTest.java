package me.zilzu.mycoupon.application.service;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CouponRepositoryTest {

    @Test
    @DisplayName("저장되어있지 않은 coupon id 를 가져올 때 Exception 발생")
    void test1() {
        CouponRepository sut = new CouponRepository();
        assertThatThrownBy(() -> {
            sut.retrieve("12312323");
        }).isInstanceOf(Exception.class);
    }

}