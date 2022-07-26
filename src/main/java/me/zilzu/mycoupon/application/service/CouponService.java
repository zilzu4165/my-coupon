package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Service;

@Service
public class CouponService {

    public CouponRetrieveResult retrieve(String id) {
        return new CouponRetrieveResult(id,"coupon",null,System.currentTimeMillis(),"usd",
                "repeating",3, false,
                null,"25.5% off",25.5F,true);
    }

}
