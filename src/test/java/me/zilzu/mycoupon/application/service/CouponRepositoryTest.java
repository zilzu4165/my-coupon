package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponRepositoryTest {

    @Test
    @DisplayName("저장되어있지 않은 coupon id 를 가져올 때 Exception 발생")
    void test1() {
        CouponRepository sut = new CouponRepository();
        assertThatThrownBy(() -> {
            sut.retrieve(new CouponId("123"));
        }).isInstanceOf(Exception.class);
    }

}