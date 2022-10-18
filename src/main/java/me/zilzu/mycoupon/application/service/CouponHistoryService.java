package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponHistoryId;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponHistoryRepository;
import me.zilzu.mycoupon.storage.CouponUsageHistoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponHistoryService {

    private final CouponHistoryRepository couponHistoryRepository;

    public CouponHistoryService(CouponHistoryRepository couponHistoryRepository) {
        this.couponHistoryRepository = couponHistoryRepository;
    }

    public CouponHistory retrieveCouponHistory(CouponHistoryId historyId) {
        CouponUsageHistoryEntity historyEntity = couponHistoryRepository.findCouponHistory(historyId);
        return new CouponHistory(historyEntity.id, new CouponId(historyEntity.refCouponId), historyEntity.usageTime, historyEntity.price, historyEntity.discountedPrice);
    }

    public List<CouponHistory> retrieveCouponHistoryList(CouponId couponId) {
        List<CouponUsageHistoryEntity> histories = couponHistoryRepository.find(couponId);
        return histories
                .stream()
                .map(entity -> new CouponHistory(entity.id, new CouponId(entity.refCouponId), entity.usageTime, entity.price, entity.discountedPrice))
                .collect(Collectors.toList());
    }
}
