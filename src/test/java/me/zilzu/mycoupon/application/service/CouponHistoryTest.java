package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponHistoryId;
import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, CouponCurrency.KRW, 1000L, null);
        Coupon coupon = couponService.create(couponCreationRequest);
        Double price = 1000d;
        CouponApplicationResult couponHistory = couponService.apply(coupon.id, price);
        List<CouponHistory> couponHistories = couponHistoryService.retrieveCouponHistoryList(couponHistory.couponId);

        assertThat(couponHistories.size()).isEqualTo(1);
        assertThat(couponHistories.get(0)).isNotNull();
        assertThat(couponHistories.get(0).refCouponId).isEqualTo(coupon.id);
    }

    @Test
    @DisplayName("없는 쿠폰의 사용이력을 조회 하려고 하면 Exception 발생")
    void test2() {
        assertThatThrownBy(() -> {
            couponHistoryService.retrieveCouponHistory(new CouponHistoryId("zilzu"));
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("couponDuration이 ONCE 인 쿠폰을 두 번 사용하려고 하면 RuntimeException 발생")
    void test3() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, CouponCurrency.KRW, 10000L, null);
        Coupon coupon = couponService.create(couponCreationRequest);
        Double price = 1000d;
        couponService.apply(coupon.id, price);

        assertThatThrownBy(() -> {
            couponService.apply(coupon.id, price);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("couponDuration이 ONCE가 아닌 쿠폰은 사용 할 때마다 couponHistory 테이블에 저장된다.")
    void test4() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, null, DiscountType.AMOUNT, CouponCurrency.KRW, 1000L, null);
        Coupon coupon = couponService.create(couponCreationRequest);
        Double price = 1000d;
        couponService.apply(coupon.id, price);
        couponService.apply(coupon.id, price);

        List<CouponHistory> couponHistories = couponHistoryService.retrieveCouponHistoryList(coupon.id);
        assertThat(couponHistories.size()).isEqualTo(2);
        assertThat(couponHistories.get(0).refCouponId).isEqualTo(coupon.id);
        assertThat(couponHistories.get(1).refCouponId).isEqualTo(coupon.id);
    }
}
