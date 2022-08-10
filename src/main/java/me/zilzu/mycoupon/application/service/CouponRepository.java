package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

@Component
public class CouponRepository {
    private Map<String, Coupon> database = new ConcurrentHashMap<>();  // db를 대체할 Map, 멀티쓰레드환경에서는 ConcurrentHashMap

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

    public Long getAllCouponSize() {
        return (long) database.size();
    }

    public void emptyCoupon() {
        database.clear();
    }

    public List<Coupon> selectRecently(Integer limit) {

        return database.values()
                .stream()
                .sorted(comparing(Coupon::getDate)
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());

    }
}
