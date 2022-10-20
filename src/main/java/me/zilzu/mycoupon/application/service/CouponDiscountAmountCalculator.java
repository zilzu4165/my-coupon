package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CouponDiscountAmountCalculator {
    public Double calculate(Coupon foundCoupon, Double price) {
        Objects.requireNonNull(foundCoupon);
        Objects.requireNonNull(price);

        Double result = 0.0;
        if (Optional.ofNullable(foundCoupon.amountOff).isPresent()) {
            result = Double.valueOf(foundCoupon.amountOff);
            if (foundCoupon.amountOff > price) result = price;

        } else if (Optional.ofNullable(foundCoupon.percentOff).isPresent()) {
            result = price * (foundCoupon.percentOff / 100);
        }
        return result;
    }
}
