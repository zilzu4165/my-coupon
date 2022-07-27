package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService {

    public Coupon retrieve(String id) {
        return new Coupon(id, "coupon", null, System.currentTimeMillis() / 1000, "usd",
                "repeating", 3, false,
                null, "25.5% off", 25.5F, true);
    }

    public List<Coupon> retrieveList(Integer limit) {

        List<Coupon> coupons = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            coupons.add(new Coupon("Z4OV52SU", "coupon", null, System.currentTimeMillis() / 1000, "usd",
                    "repeating", 3, false,
                    null, "25.5% off", 25.5F, true));
        }

        return coupons;
    }
}
