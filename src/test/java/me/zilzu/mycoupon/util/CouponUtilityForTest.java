package me.zilzu.mycoupon.util;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.application.service.CouponIdGenerator;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;

import java.time.LocalDateTime;

public class CouponUtilityForTest {

    public static Coupon newInstance() {
        return new Coupon(new CouponId(new CouponIdGenerator().generate()),
                CouponDuration.ONCE,
                null,
                Currency.USD,
                DiscountType.PERCENTAGE,
                null,
                30.0,
                true,
                LocalDateTime.now());
    }

    public static Coupon createCouponFrom(DiscountType discountType, Long amountOff, Double percentOff) {
        return new Coupon(new CouponId(new CouponIdGenerator().generate()), CouponDuration.ONCE, null, null, discountType, amountOff, percentOff, null, null);
    }
}
