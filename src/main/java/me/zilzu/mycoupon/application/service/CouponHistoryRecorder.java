package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponHistoryRepository;
import me.zilzu.mycoupon.storage.CouponUsageHistoryEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CouponHistoryRecorder {

    private final CouponHistoryIdGenerator couponHistoryIdGenerator;
    private final CouponHistoryRepository couponHistoryRepository;

    public CouponHistoryRecorder(CouponHistoryIdGenerator couponHistoryIdGenerator,
                                 CouponHistoryRepository couponHistoryRepository) {
        this.couponHistoryIdGenerator = couponHistoryIdGenerator;
        this.couponHistoryRepository = couponHistoryRepository;
    }
}
