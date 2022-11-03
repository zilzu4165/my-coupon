package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponCacheTest {

    @Autowired
    CouponService sut;

    @BeforeEach
    void emptyCoupon() {
        sut.emptyCoupon();
    }


    @Test
    @DisplayName("Coupon이 Cache에 저장되는지 확인한다.")
    void test() {
        CouponCreationRequest request = new CouponCreationRequest(
                CouponDuration.REPEATING
                , 3
                , DiscountType.AMOUNT
                , null
                , 10L
                , null
        );
        Coupon coupon = sut.create(request, LocalDateTime.now());

        Coupon foundCouponFromDB = sut.retrieve(coupon.id);
        Coupon foundsCouponFromCache = sut.retrieve(coupon.id);
        System.out.println(coupon.id);

        assertThat(foundsCouponFromCache.id).isEqualTo(foundCouponFromDB.id);
    }
}