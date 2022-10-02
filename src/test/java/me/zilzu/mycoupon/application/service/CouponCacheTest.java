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
    private CouponService sut; // system under test

    @Test
    @DisplayName("id 에 대해서 cache 에 저장되는지 확인한다.")
    void cache() {
        CouponCreationRequest request = new CouponCreationRequest(
                CouponDuration.FOREVER,
                null,
                DiscountType.AMOUNT,
                10L,
                null
        );

        Coupon newCoupon = sut.create(request);
        System.out.println(newCoupon.id);

        Coupon foundCouponFromDB = sut.retrieve(newCoupon.id);
        Coupon foundCouponFromCache = sut.retrieve(newCoupon.id);

        assertThat(foundCouponFromCache).isEqualTo(foundCouponFromDB);
    }
}