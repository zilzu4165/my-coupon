package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponCurrency;
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

    public CouponHistory record(CouponId couponId, Double price, Double discountedPrice) {
        return record(couponId, price, discountedPrice, CouponCurrency.KRW);
    }

    public CouponHistory record(CouponId couponId, Double price, Double discountedPrice, CouponCurrency couponCurrency) {
        CouponUsageHistoryEntity historyEntity = new CouponUsageHistoryEntity(couponHistoryIdGenerator.generate(), couponId.value, LocalDateTime.now(), CouponCurrency.KRW, price, discountedPrice);
        couponHistoryRepository.save(historyEntity);

        return new CouponHistory(historyEntity.id, new CouponId(historyEntity.refCouponId), historyEntity.usageTime, historyEntity.couponCurrency, price, discountedPrice);
    }
}
