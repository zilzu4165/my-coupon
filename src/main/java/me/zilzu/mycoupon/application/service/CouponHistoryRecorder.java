package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponHistoryRepository;
import me.zilzu.mycoupon.storage.CouponUsageHistoryEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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

    public CouponHistory record(Coupon coupon, Double price, BigDecimal discountedPrice) {
        CouponUsageHistoryEntity historyEntity = new CouponUsageHistoryEntity(couponHistoryIdGenerator.generate(), coupon.id.value, LocalDateTime.now(), coupon.currency, price, discountedPrice);
        couponHistoryRepository.save(historyEntity);

        return new CouponHistory(historyEntity.id, new CouponId(historyEntity.refCouponId), historyEntity.usageTime, historyEntity.currency, price, discountedPrice);
    }
}
