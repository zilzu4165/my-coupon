package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponValidatorTest {

    private final CouponValidator sut = new CouponValidator();

    @Test
    @DisplayName("쿠폰 생성요청에서 할인 유형이 Amount(정량) 인데, 몇퍼센트를 할인해줄지 정보가 있으면 IllegalArgumentException 을 발생시킨다.")
    void test1() {
        CouponCreationRequest request = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.AMOUNT, 1000L, 1000.0);

        assertThatThrownBy(() -> {
            sut.validate(request);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}