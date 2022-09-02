package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CouponApplyTest {

    @Autowired
    CouponService couponService;

    @DisplayName("쿠폰을 사용하면 쿠폰이 사용되었다는 응답이 와야한다.")
    @Test
    void test1() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponRequest);

        String apply = couponService.apply(coupon.id);

        assertThat(apply).isEqualTo("사용됨");
    }

}
