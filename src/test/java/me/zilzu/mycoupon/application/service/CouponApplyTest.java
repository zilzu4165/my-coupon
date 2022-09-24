package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class CouponApplyTest {

    @Autowired
    CouponService couponService;

    @DisplayName("duration이 ONCE 유형인 쿠폰은 사용후 valid가 false로 변경된다.")
    @Test
    void test2() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponRequest);

        couponService.apply(coupon.id);

        Coupon foundCoupon = couponService.retrieve(coupon.id);

        assertThat(foundCoupon.valid).isFalse();
    }

    @DisplayName("없는 쿠폰을 사용하려고 하면 IllegalArgumentException 발생")
    @Test
    void test3() {
        assertThatThrownBy(() -> {
            couponService.apply("zilzu");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Duration 이 ONCE인 쿠폰을 두번 사용하려고 하면 Exception 발생")
    @Test
    void test4() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);

        Coupon coupon = couponService.create(couponRequest);
        Coupon foundCoupon = couponService.retrieve(coupon.id);
        couponService.apply(foundCoupon.id);  // valid false

        assertThatThrownBy(() -> {
            couponService.apply(coupon.id);
        }).isInstanceOf(RuntimeException.class);
    }
}
