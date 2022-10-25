package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponDiscountAmountCalculator;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponDiscountAmountCalculatorTest {

    CouponDiscountAmountCalculator sut = new CouponDiscountAmountCalculator();

    @Test
    @DisplayName("DiscountType이 AMOUNT일 경우 amount양 만큼 계산된다.")
    void test1() {
        BigDecimal discountedPrice = sut.getDiscountedPrice(createCoupon(DiscountType.AMOUNT, 1000L, null), 10000d);

        assertThat(discountedPrice.doubleValue()).isEqualTo(9000);
    }

    @Test
    @DisplayName("DiscountType이 PERCENTAGE일 경우 percentage 비율만큼 계산된다.")
    void test2() {
        BigDecimal discountedPrice = sut.getDiscountedPrice(createCoupon(DiscountType.PERCENTAGE, 0, 20d), 10000d);


        assertThat(discountedPrice.doubleValue()).isEqualTo(8000);
    }

    @Test
    @DisplayName("할인 가격이 null일 경우 RuntimeException 발생")
    void test3() {
        assertThatThrownBy(() -> {
            sut.getDiscountedPrice(null, null);
        }).isInstanceOf(RuntimeException.class);

    }

    private static Coupon createCoupon(DiscountType discountType, long amountOff, Double percentOff) {
        return new Coupon(new CouponId(new CouponIdGenerator().generate()), CouponDuration.ONCE, null, null, discountType, amountOff, percentOff, null, null);
    }
}