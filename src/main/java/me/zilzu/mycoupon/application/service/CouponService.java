package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Service;

@Service
public class CouponService {

    public Coupon retrieve(String id) {
        return new Coupon(id,"coupon",null,System.currentTimeMillis()/1000,"usd",
                "repeating",3, false,
                null,"25.5% off",25.5F,true);
    }

}
