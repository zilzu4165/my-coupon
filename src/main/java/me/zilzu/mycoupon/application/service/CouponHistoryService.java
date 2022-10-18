package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponHistoryId;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponUsageHistoryEntity;
import me.zilzu.mycoupon.storage.CouponUsageHistoryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponHistoryService {

    private final CouponUsageHistoryRepository couponUsageHistoryRepository;
    private final CouponHistoryIdGenerator couponHistoryIdGenerator;

    public CouponHistoryService(CouponUsageHistoryRepository couponHistoryRepository, CouponHistoryIdGenerator couponHistoryIdGenerator) {
        this.couponUsageHistoryRepository = couponHistoryRepository;
        this.couponHistoryIdGenerator = couponHistoryIdGenerator;
    }

    public CouponHistory retrieveCouponHistory(CouponHistoryId historyId) {
        CouponUsageHistoryEntity historyEntity = Optional.ofNullable(couponUsageHistoryRepository.findById(historyId.value)).orElseThrow().get();
        return new CouponHistory(historyEntity.id, new CouponId(historyEntity.refCouponId), historyEntity.usageTime, historyEntity.price, historyEntity.discountedPrice);
    }

    public List<CouponHistory> retrieveCouponHistoryList(CouponId couponId) {
        List<CouponUsageHistoryEntity> histories = couponUsageHistoryRepository.findByRefCouponId(couponId.value);
        return histories
                .stream()
                .map(entity -> new CouponHistory(entity.id, new CouponId(entity.refCouponId), entity.usageTime, entity.price, entity.discountedPrice))
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponHistory saveApplicationHistory(CouponId couponId, Double price, Double discountedPrice) {
        CouponUsageHistoryEntity historyEntity = new CouponUsageHistoryEntity(couponHistoryIdGenerator.generate(), couponId.value, LocalDateTime.now(), price, discountedPrice);
        couponUsageHistoryRepository.save(historyEntity);
        return new CouponHistory(historyEntity.id, couponId, historyEntity.usageTime, historyEntity.price, historyEntity.discountedPrice);
    }
}
