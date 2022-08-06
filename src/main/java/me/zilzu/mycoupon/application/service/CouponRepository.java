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
}
