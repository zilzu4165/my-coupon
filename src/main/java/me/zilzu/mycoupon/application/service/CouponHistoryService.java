package me.zilzu.mycoupon.application.service;

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

    public CouponHistory retrieveCouponHistory(String historyId) {
        CouponUsageHistoryEntity historyEntity = couponHistoryRepository.findCouponHistory(historyId);
        return new CouponHistory(historyEntity.id, historyEntity.refCouponId, historyEntity.usageTime);
    }

    public List<CouponHistory> retrieveCouponHistoryList(String couponId) {
        List<CouponUsageHistoryEntity> histories = couponHistoryRepository.find(couponId);
        return histories
                .stream()
                .map(entity -> new CouponHistory(entity.id, entity.refCouponId, entity.usageTime))
                .collect(Collectors.toList());
    }
}
