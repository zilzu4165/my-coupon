package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    CouponService couponService;

    @Test
    void coupon_create_and_retrieve_test() {
        CouponRequest request = new CouponRequest(CouponDuration.ONCE, null);
        Coupon coupon = couponService.create(request);

        Coupon retrievedCoupon = couponService.retrieve(coupon.id);

        assertThat(retrievedCoupon).isEqualTo(coupon);
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰을 조회할 때는 exception이 터진다. IllegalaragumentException")
    void test2() {
        assertThatThrownBy(() -> {
            Coupon coupon = couponService.retrieve("12345678");
            System.out.println("coupon = " + coupon);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}