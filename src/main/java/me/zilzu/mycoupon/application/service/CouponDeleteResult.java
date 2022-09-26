package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;

public class CouponDeleteResult {

    public CouponId deletedCouponId;

    public CouponDeleteResult(CouponId deletedCouponId) {
        this.deletedCouponId = deletedCouponId;
    }
}
