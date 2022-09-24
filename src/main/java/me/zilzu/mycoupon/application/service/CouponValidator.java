package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.springframework.stereotype.Component;

@Component
public class CouponValidator {

    public void validate(CouponRequest couponRequest) {
        if (couponRequest.duration != CouponDuration.REPEATING && couponRequest.durationInMonths != null) {
            throw new IllegalArgumentException("duration이 REPEATING 유형이 아니라면 durationInMonths 값을 가질 수 없습니다");
        }
        if (couponRequest.amountOff != null && couponRequest.percentOff != null) {
            throw new IllegalArgumentException("금액할인과 비율할인이 동시에 값을 가질 수 없습니다.");
        }
        if (couponRequest.discountType == DiscountType.AMOUNT && couponRequest.amountOff == null) {
            throw new IllegalArgumentException("discountType이 AMOUNT일 경우 amountOff 에 값이 존재해야 합니다.");
        }
        if (couponRequest.discountType == DiscountType.AMOUNT && couponRequest.percentOff != null) {
            throw new IllegalArgumentException("discountType이 AMOUNT일 경우 percentOff 에 값이 존재할 수 없습니다.");
        }
        if (couponRequest.discountType == DiscountType.PERCENTAGE && couponRequest.percentOff == null) {
            throw new IllegalArgumentException("discountType이 PERCENTAGE일 경우 percentOff 에 값이 존재해야 합니다.");
        }
        if (couponRequest.discountType == DiscountType.PERCENTAGE && couponRequest.amountOff != null) {
            throw new IllegalArgumentException("discountType이 PERCENTAGE일 경우 amountOff 에 값이 존재할 수 없습니다.");
        }
    }
}
