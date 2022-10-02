package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponCacheTest {

    @Autowired
    CouponService sut;

    @Test
    @DisplayName("")
    void test() {
        CouponCreationRequest request = new CouponCreationRequest(
                CouponDuration.REPEATING
                , 3
                , DiscountType.AMOUNT
                , 10L
                , null
        );
        Coupon coupon = sut.create(request);

        Coupon foundCouponFromDB = sut.retrieve(coupon.id);
        Coupon foundsCouponFromCache = sut.retrieve(coupon.id);

        assertThat(foundsCouponFromCache.id).isEqualTo(foundCouponFromDB.id);

    }
}