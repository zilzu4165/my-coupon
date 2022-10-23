package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.DiscountType;
import me.zilzu.mycoupon.util.CouponUtilityForTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CouponDiscountAmountCalculatorTest {

    @Test
    @DisplayName("쿠폰 할인가격에 대한 정확성이 요구될 경우 BigDecimal로 반환한다.")
    void coupon_discounted_price_with_accuracy() {
        CouponDiscountAmountCalculator couponDiscountAmountCalculator = new CouponDiscountAmountCalculator();

        Coupon coupon = CouponUtilityForTest.createCouponFrom(DiscountType.PERCENTAGE, null, 31.1234123123);

        Double price = 125.125; // USD
        Double resultNormal = couponDiscountAmountCalculator.calculate(coupon, price);
        BigDecimal resultAccuracy = couponDiscountAmountCalculator.calculateAccuracy(coupon, price);

        System.out.println("resultNormal = " + resultNormal);
        System.out.println("resultAccuracy = " + resultAccuracy);

        assertThat(String.valueOf(resultAccuracy)).isNotEqualTo(String.valueOf(resultNormal));
    }



}