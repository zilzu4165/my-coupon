package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponDiscountAmountCalculator;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponDiscountAmountCalculatorTest {

    CouponDiscountAmountCalculator sut = new CouponDiscountAmountCalculator();

    @Test
    @DisplayName("DiscountType이 AMOUNT일 경우 amount양 만큼 계산된다.")
    void test1() {
        Double discountedPrice = sut.getDiscountedPrice(10000d, createCoupon(DiscountType.AMOUNT, 1000L, null));

        assertThat(discountedPrice).isEqualTo(9000);
    }

    @Test
    @DisplayName("DiscountType이 PERCENTAGE일 경우 percentage 비율만큼 계산된다.")
    void test2() {
        Double discountedPrice = sut.getDiscountedPrice(10000d, createCoupon(DiscountType.PERCENTAGE, 0, 20d));

        assertThat(discountedPrice).isEqualTo(8000);
    }

    private static Coupon createCoupon(DiscountType discountType, long amountOff, Double percentOff) {
        return new Coupon(new CouponId(new CouponIdGenerator().generate()), CouponDuration.ONCE, null, null, discountType, amountOff, percentOff, null, null);
    }
}