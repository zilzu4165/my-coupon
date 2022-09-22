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

    @Autowired
    CouponHistoryService couponHistoryService;

    @Test
    @DisplayName("쿠폰 사용시 쿠폰 사용을 저장하는 쿠폰 history 테이블에도 저장된다.")
    void test1() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponRequest);

        CouponHistory couponHistory = couponService.apply(coupon.id);

        CouponHistory history = couponHistoryService.retrieveCouponHistory(couponHistory.id);
        assertThat(history).isNotNull();
        assertThat(history.refCouponId).isEqualTo(coupon.id);
    }

    @Test
    @DisplayName("없는 쿠폰의 사용이력을 조회 하려고 하면 Exception 발생")
    void test2() {
        assertThatThrownBy(() -> {
            couponHistoryService.retrieveCouponHistory("zilzu");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("couponDuration이 ONCE 인 쿠폰을 두 번 사용하려고 하면 RuntimeException 발생")
    void test3() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponRequest);

        couponService.apply(coupon.id);

        assertThatThrownBy(() -> {
            couponService.apply(coupon.id);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("couponDuration이 ONCE가 아닌 쿠폰은 사용 할 때마다 couponHistory 테이블에 저장된다.")
    void test4() {
        CouponRequest couponRequest = new CouponRequest(CouponDuration.REPEATING, null, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponRequest);

        CouponHistory couponHistory = couponService.apply(coupon.id);
        CouponHistory couponHistory2 = couponService.apply(coupon.id);

        CouponHistory foundCouponHistory1 = couponHistoryService.retrieveCouponHistory(couponHistory.id);
        CouponHistory foundCouponHistory2 = couponHistoryService.retrieveCouponHistory(couponHistory2.id);
        assertThat(foundCouponHistory1.refCouponId).isEqualTo(foundCouponHistory2.refCouponId);
    }
}
