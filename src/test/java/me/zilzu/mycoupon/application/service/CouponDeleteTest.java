package me.zilzu.mycoupon.application.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class CouponDeleteTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponIdGenerate couponIdGenerate;

    @Test
    @DisplayName("쿠폰의 ID값이 주어졌을 때, 해당 ID 값에 해당하는 쿠폰을 repository에서 삭제한다.")
    public void test1() {
        CouponRequest couponRequest = new CouponRequest("3", 3);
        Coupon coupon = couponService.create(couponRequest);
        String id = coupon.id;

        Coupon deletedCoupon = couponService.delete(id);

        Assertions.assertThat(deletedCoupon.id).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 ID값을 삭제하려하면 Exception을 발생시킨다.")
    public void test2() {
        String notExistId = "ZILZU";

        assertThatThrownBy(() -> {
            couponService.delete(notExistId);
        }).isInstanceOf(RuntimeException.class);

    }
}
