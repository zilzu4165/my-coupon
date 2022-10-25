package me.zilzu.mycoupon.common;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CouponDiscountAmountCalculator {
    public BigDecimal getDiscountedPrice(Coupon foundCoupon, Double price) {
        BigDecimal discountedPrice = null;
        BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);

        if (foundCoupon.discountType == DiscountType.AMOUNT) {
            discountedPrice = BigDecimal.valueOf(price - foundCoupon.amountOff);
        } else if (foundCoupon.discountType == DiscountType.PERCENTAGE) {
            BigDecimal bigDecimalPercentOff = BigDecimal.valueOf(foundCoupon.percentOff);
            discountedPrice = bigDecimalPrice.subtract(bigDecimalPrice.multiply(bigDecimalPercentOff.divide(BigDecimal.valueOf(100))));
        }
        if (discountedPrice == null) {
            throw new RuntimeException("할인 가격을 계산하던 중 문제가 발생했습니다.");
        }
        return discountedPrice;
    }
}
