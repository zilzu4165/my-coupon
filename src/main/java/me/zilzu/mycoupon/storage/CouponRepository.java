package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.enums.SortingOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Component
public class CouponRepository {
    private Map<String, CouponEntity> database = new ConcurrentHashMap<>();  // db를 대체할 Map, 멀티쓰레드환경에서는 ConcurrentHashMap

    public void save(CouponEntity coupon) {
        if (database.containsKey(coupon.id)) {
            throw new RuntimeException("Duplicate key");
        }
        database.put(coupon.id, coupon);
    }


    public CouponEntity retrieve(String id) {
        CouponEntity coupon = database.get(id);
        if (coupon == null) {
            throw new IllegalArgumentException("해당하는 coupon id가 없습니다.");
        }
        return coupon;
    }

    public Long getAllCouponSize() {
        return (long) database.size();
    }

    public void emptyCoupon() {
        database.clear();
    }

    public List<CouponEntity> selectRecently(Integer limit, SortingOrder sortedBy) {
        List<CouponEntity> sortedCoupons = null;

        if (sortedBy == SortingOrder.ASC) {
            sortedCoupons = database.values()
                    .stream()
                    .sorted(comparing(CouponEntity::getCreatedTime))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if (sortedBy == SortingOrder.DESC) {
            sortedCoupons = database.values()
                    .stream()
                    .sorted(comparing(CouponEntity::getCreatedTime)
                            .reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        return sortedCoupons;
    }
}
