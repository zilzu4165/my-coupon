package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.SortingOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Component
public class CouponRepository {
    private Map<String, CouponEntity> database = new ConcurrentHashMap<>();  // db를 대체할 Map, 멀티쓰레드환경에서는 ConcurrentHashMap


    public void save(CouponEntity coupon) {
        if (database.containsKey(coupon.id.value)) {
            throw new RuntimeException("Duplicate key");
        }
        database.put(coupon.id.value, coupon);
    }


    public CouponEntity retrieve(CouponId id) {
        CouponEntity coupon = database.get(id.value);
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
                    .sorted(comparing(entity -> entity.createdTime))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if (sortedBy == SortingOrder.DESC) {
            sortedCoupons = database.values()
                    .stream()
                    .sorted(comparing((Function<CouponEntity, LocalDateTime>) couponEntity -> couponEntity.createdTime)
                            .reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        return sortedCoupons;
    }

    public CouponEntity delete(CouponId id) {
        if (!database.containsKey(id.value)) {
            throw new RuntimeException("존재하지 않는 id 입니다.");
        }
        return database.remove(id.value);
    }

    public void invalidate(CouponId couponId) {
        CouponEntity entity = database.get(couponId.value);
        entity.valid = false;
        database.put(entity.id.value, entity);
    }
}
