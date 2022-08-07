package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CouponRepository {
    Map<String, Coupon> database = new HashMap<>();  // db를 대체할 Map

    public void save(Coupon coupon) {
        if (database.containsKey(coupon.id)) {
            throw new RuntimeException("Duplicate key");
        }

        database.put(coupon.id, coupon);
    }


    public Coupon retrieve(String id) {
        Coupon coupon = database.get(id);

        if (coupon == null) {
            throw new IllegalArgumentException("해당하는 coupon id가 없습니다.");
        }
        
        return coupon;
    }

    public Long getAllCoupon() {
        return (long) database.size();
    }

    public void emptyCoupon() {
        database.clear();
    }
}
