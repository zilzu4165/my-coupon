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
public class CouponHistoryTest {

    @Autowired
    CouponService couponService;

    @Test
    @DisplayName("쿠폰 사용시 쿠폰 사용을 저장하는 쿠폰 history 테이블에도 저장된다.")
    void test1() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponRequest);

        couponService.apply(coupon.id);

        CouponHistory history = couponService.retrieveCouponHistory(coupon.id);
        assertThat(history).isNotNull();
        assertThat(history.id).isEqualTo(coupon.id);
    }

    @Test
    @DisplayName("없는 쿠폰의 사용이력을 조회 하려고 하면 Exception 발생")
    void test2() {
        assertThatThrownBy(() -> {
            couponService.retrieveCouponHistory("zilzu");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
