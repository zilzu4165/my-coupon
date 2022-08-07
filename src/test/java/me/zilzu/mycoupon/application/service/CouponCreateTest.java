package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class CouponCreateTest {

    @Autowired
    CouponService couponService;


    @Test
    @DisplayName("쿠폰 100개가 동시에 생성됐을 때, 100개가 정상적으로 생성된다.")
    void test() {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ArrayList<Coupon> coupons = new ArrayList<>();

        // 동시에 100개 생성
        for (int i = 0; i < 100; i++) {
            Coupon coupon = couponService.create(couponRequest);
            coupons.add(coupon);
        }

        Assertions.assertThat(coupons.size()).isEqualTo(100);

        coupons.forEach(System.out::println);
    }
}
