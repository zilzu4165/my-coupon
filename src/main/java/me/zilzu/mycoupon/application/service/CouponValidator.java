package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.springframework.stereotype.Component;

@Component
public class CouponValidator {

    public void validate(CouponCreationRequest couponCreationRequest) {
        if (couponCreationRequest.duration != CouponDuration.REPEATING && couponCreationRequest.durationInMonths != null) {
            throw new CouponValidException("duration이 REPEATING 유형이 아니라면 durationInMonths 값을 가질 수 없습니다");
        }
        if (couponCreationRequest.amountOff != null && couponCreationRequest.percentOff != null) {
            throw new CouponValidException("금액할인과 비율할인이 동시에 값을 가질 수 없습니다.");
        }
        if (couponCreationRequest.discountType == DiscountType.AMOUNT && couponCreationRequest.amountOff == null) {
            throw new CouponValidException("discountType이 AMOUNT일 경우 amountOff 에 값이 존재해야 합니다.");
        }
        if (couponCreationRequest.discountType == DiscountType.AMOUNT && couponCreationRequest.percentOff != null) {
            throw new CouponValidException("discountType이 AMOUNT일 경우 percentOff 에 값이 존재할 수 없습니다.");
        }
        if (couponCreationRequest.discountType == DiscountType.PERCENTAGE && couponCreationRequest.percentOff == null) {
            throw new CouponValidException("discountType이 PERCENTAGE일 경우 percentOff 에 값이 존재해야 합니다.");
        }
        if (couponCreationRequest.discountType == DiscountType.PERCENTAGE && couponCreationRequest.amountOff != null) {
            throw new CouponValidException("discountType이 PERCENTAGE일 경우 amountOff 에 값이 존재할 수 없습니다.");
        }

    }
}
