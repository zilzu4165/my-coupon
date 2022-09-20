package me.zilzu.mycoupon.storage;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CouponHistoryRepository {

    private Map<String, CouponUsageHistoryEntity> historyManagementDatabase = new ConcurrentHashMap<>();

    public void save(CouponUsageHistoryEntity entity) {
        historyManagementDatabase.put(entity.id, entity);
    }

    public CouponUsageHistoryEntity selectCouponUsageHistory(String id) {
        CouponUsageHistoryEntity historyEntity = historyManagementDatabase.get(id);
        if (historyEntity == null) {
            throw new IllegalArgumentException("해당하는 coupon id가 없습니다.");
        }
        return historyEntity;
    }

}
