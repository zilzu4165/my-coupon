package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class CouponApplyTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponHistoryService couponHistoryService;

    @BeforeEach
    void emptyCoupon() {
        couponService.emptyCoupon();
    }

    @DisplayName("duration이 ONCE 유형인 쿠폰은 사용후 valid가 false로 변경된다.")
    @Test
    void test2() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, Currency.KRW, 1000L, null);
        Coupon coupon = couponService.create(couponCreationRequest, LocalDateTime.now());
        Double price = 1000d;
        couponService.apply(coupon.id, price);

        Coupon foundCoupon = couponService.retrieve(coupon.id);

        assertThat(foundCoupon.valid).isFalse();
    }

    @DisplayName("없는 쿠폰을 사용하려고 하면 IllegalArgumentException 발생")
    @Test
    void test3() {
        assertThatThrownBy(() -> {
            couponService.apply(new CouponId("zilzu"), 100d);
        }).isInstanceOf(Exception.class);
    }

    @DisplayName("Duration 이 ONCE인 쿠폰을 두번 사용하려고 하면 Exception 발생")
    @Test
    void test4() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, Currency.KRW, 1000L, null);

        Coupon coupon = couponService.create(couponCreationRequest, LocalDateTime.now());
        Coupon foundCoupon = couponService.retrieve(coupon.id);
        couponService.apply(foundCoupon.id, 1000d);  // valid false

        assertThatThrownBy(() -> {
            couponService.apply(coupon.id, 1000d);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("쿠폰 history 에 할인된 가격이 표시된다. DiscountType AMOUNT 일 때")
    void test5() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, Currency.KRW, 10000L, null);

        Coupon coupon = couponService.create(couponCreationRequest, LocalDateTime.now());

        Double price = 20000d;
        couponService.apply(coupon.id, price);

        List<CouponHistory> couponHistories = couponHistoryService.retrieveCouponHistoryList(coupon.id);

        for (CouponHistory couponHistory : couponHistories) {
            assertThat(couponHistory.price).isEqualTo(price);
            assertThat(couponHistory.discountedPrice.doubleValue()).isEqualTo(10000);
        }
    }

    @Test
    @DisplayName("쿠폰 history 에 할인된 가격이 표시된다. DiscountType PERCENTAGE 일 때")
    void test6() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.PERCENTAGE, Currency.KRW, null, 10d);

        Coupon coupon = couponService.create(couponCreationRequest, LocalDateTime.now());

        Double price = 10000d;
        couponService.apply(coupon.id, price);

        List<CouponHistory> couponHistories = couponHistoryService.retrieveCouponHistoryList(coupon.id);

        for (CouponHistory couponHistory : couponHistories) {
            assertThat(couponHistory.price).isEqualTo(price);
            assertThat(couponHistory.discountedPrice.doubleValue()).isEqualTo(9000);
            assertThat(couponHistory.currency).isEqualTo(Currency.KRW);
        }
    }

    @Test
    @DisplayName("couponHistory 의 CouponCurrency가 정상적으로 조회된다.")
    void test7() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.PERCENTAGE, Currency.USD, null, 10d);
        Coupon coupon = couponService.create(couponCreationRequest, LocalDateTime.now());

        Double price = 10000d;
        couponService.apply(coupon.id, price);

        List<CouponHistory> couponHistories = couponHistoryService.retrieveCouponHistoryList(coupon.id);

        for (CouponHistory couponHistory : couponHistories) {
            assertThat(couponHistory.currency).isEqualTo(couponCreationRequest.currency);
        }
    }
}
