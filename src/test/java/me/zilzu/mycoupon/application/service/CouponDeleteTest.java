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

        CouponDeleteResult deletedCoupon = couponService.delete(coupon.id);

        Assertions.assertThat(deletedCoupon.deletedCouponId).isEqualTo(coupon.id);
    }

    @Test
    @DisplayName("삭제된 쿠폰 id 값을 조회하려하면 IllegalArgumentException이 발생한다.")
    public void test2() {
        CouponRequest couponRequest = new CouponRequest("3", 3);
        Coupon coupon = couponService.create(couponRequest);

        couponService.delete(coupon.id);

        assertThatThrownBy(() -> {
            couponService.retrieve(coupon.id);
        }).isInstanceOf(IllegalArgumentException.class);

    }
}
