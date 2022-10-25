package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponValidatorTest {
    private final CouponValidator sut = new CouponValidator();


    @Test
    @DisplayName("쿠폰 생성요청에서 할인 유형이 Amount(정량) 인데, 몇퍼센트를 할인해줄지 정보가 있으면 IllegalArgumentException 을 발생시킨다.")
    void test() {
        CouponCreationRequest request = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.AMOUNT, Currency.KRW, 1000L, 1000.0);

        assertThatThrownBy(() -> {
            sut.validate(request);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("쿠폰 생성요청시 할인유형이 PERCENTAGE(비율할인) 라면 amountOff 에 정보가 있으면 IllegaArgumentException을 발생시킨다.")
    @Test
    void test1() {
        CouponCreationRequest request = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.PERCENTAGE, Currency.KRW, 1000L, 1000.0);

        assertThatThrownBy(() ->
                sut.validate(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("쿠폰 생성요청시 amountOff , percentOff 두 값이 동시에 존재하면 IllegalArgumentException 을 발생시킨다.")
    @Test
    void test2() {
        CouponCreationRequest request = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.AMOUNT, Currency.KRW, 1000L, 1000.0);

        assertThatThrownBy(() -> {
            sut.validate(request);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("쿠폰 생성요청시 duration이 REPEATING 유형이 아닌데 durationInMonths 정보가있으면 IllegalArgumentException 발생시킨다")
    void test3() {
        CouponCreationRequest request = new CouponCreationRequest(CouponDuration.ONCE, 3, null, Currency.KRW, null, null);

        assertThatThrownBy(() -> {
            sut.validate(request);
        }).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("쿠폰 생성시 할인유형이 AMOUNT(정량) 일 때, amountOff 에 값이 존재하지않으면 IllegalArgumentException 발생시킨다.")
    @Test
    void test4() {
        CouponCreationRequest request = new CouponCreationRequest(null, null, DiscountType.AMOUNT, Currency.KRW, null, null);

        assertThatThrownBy(() -> {
            sut.validate(request);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("쿠폰 생성시 할인유형이 PERCENTAGE(비율할인) 일 때, percentOff 에 값이 존재하지않으면 IllegalArgumentException 발생시킨다.")
    @Test
    void test5() {
        CouponCreationRequest request = new CouponCreationRequest(null, null, DiscountType.PERCENTAGE, Currency.KRW, null, null);

        assertThatThrownBy(() -> {
            sut.validate(request);
        }).isInstanceOf(IllegalArgumentException.class);

    }
}