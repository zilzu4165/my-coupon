package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponHistoryRepository;
import me.zilzu.mycoupon.storage.CouponUsageHistoryEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CouponHistoryRecorder {

    private final CouponIdGenerator couponIdGenerator;
    private final CouponHistoryRepository couponHistoryRepository;

    public CouponHistoryRecorder(CouponIdGenerator couponIdGenerator,
                                 CouponHistoryRepository couponHistoryRepository) {
        this.couponIdGenerator = couponIdGenerator;
        this.couponHistoryRepository = couponHistoryRepository;
    }

    public CouponHistory record(CouponId couponId) {
        CouponUsageHistoryEntity historyEntity = new CouponUsageHistoryEntity(couponIdGenerator.generate(), couponId.value, LocalDateTime.now());
        couponHistoryRepository.save(historyEntity);

        return new CouponHistory(historyEntity.id, new CouponId(historyEntity.refCouponId), historyEntity.usageTime);
    }
}
