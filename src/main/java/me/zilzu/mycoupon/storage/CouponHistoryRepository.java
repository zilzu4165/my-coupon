package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.CouponHistoryId;
import me.zilzu.mycoupon.common.CouponId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class CouponHistoryRepository {

    private final Map<String, CouponUsageHistoryEntity> historyManagementDatabase = new ConcurrentHashMap<>();

    public void save(CouponUsageHistoryEntity entity) {
        historyManagementDatabase.put(entity.id, entity);
    }

    public CouponUsageHistoryEntity findCouponHistory(CouponHistoryId historyId) {
        CouponUsageHistoryEntity historyEntity = historyManagementDatabase.get(historyId.value);
        if (historyEntity == null) {
            throw new IllegalArgumentException("해당 쿠폰 사용기록이 없습니다.");
        }
        return historyEntity;
    }

    public CouponUsageHistoryEntity findNullableCouponHistory(String historyId) {
        return historyManagementDatabase.get(historyId);
    }

    public List<CouponUsageHistoryEntity> find(CouponId couponId) {
        return historyManagementDatabase.values()
                .stream()
                .filter(coupon -> coupon.refCouponId.equals(couponId.value))
                .collect(Collectors.toList());
    }

}
