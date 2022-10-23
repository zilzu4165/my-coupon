package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Component
public class CouponDiscountAmountCalculator {
    public Double calculate(Coupon coupon, Double price) {
        Objects.requireNonNull(coupon);
        Objects.requireNonNull(price);

        Double result = 0.0;
        if (Optional.ofNullable(coupon.amountOff).isPresent()) {
            result = Double.valueOf(coupon.amountOff);
            if (coupon.amountOff > price) result = price;

        } else if (Optional.ofNullable(coupon.percentOff).isPresent()) {
            result = price * (coupon.percentOff / 100);
        }
        return result;
    }

    public BigDecimal calculateAccuracy(Coupon coupon, Double price) {
        Objects.requireNonNull(coupon);
        Objects.requireNonNull(price);

        BigDecimal result = BigDecimal.ZERO;

        if (Optional.ofNullable(coupon.amountOff).isPresent()) {
            result = BigDecimal.valueOf(coupon.amountOff);
            if (coupon.amountOff > price) result = BigDecimal.valueOf(price);

        } else if (Optional.ofNullable(coupon.percentOff).isPresent()) {
            BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);
            BigDecimal bigDecimalPercentOff = BigDecimal.valueOf(coupon.percentOff);
            BigDecimal bigDecimalHundred = BigDecimal.valueOf(100);
            result = bigDecimalPrice.multiply(bigDecimalPercentOff.divide(bigDecimalHundred));
        }
        return result;
    }
}
