package me.zilzu.mycoupon.common;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.springframework.stereotype.Component;

@Component
public class CouponDiscountAmountCalculator {
    public Double getDiscountedPrice(Double price, Coupon foundCoupon) {
        Double discountedPrice = null;
        if (foundCoupon.discountType == DiscountType.AMOUNT) {
            discountedPrice = price - foundCoupon.amountOff;
        } else if (foundCoupon.discountType == DiscountType.PERCENTAGE) {
            discountedPrice = price - (price * (foundCoupon.percentOff / 100));
        }
        if (discountedPrice == null) {
            throw new RuntimeException("할인 가격을 계산하던 중 문제가 발생했습니다.");
        }
        return discountedPrice;
    }
}
