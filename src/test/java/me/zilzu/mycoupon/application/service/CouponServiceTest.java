package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    CouponService couponService;

    @Test
    void coupon_create_and_retrieve_test() {
        CouponRequest request = new CouponRequest("3", 3);
        Coupon coupon = couponService.create(request);

        Coupon retrievedCoupon = couponService.retrieve(coupon.id);

        assertThat(retrievedCoupon).isEqualTo(coupon);

    }

}