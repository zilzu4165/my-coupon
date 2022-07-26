package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.CouponRetrieveResult;
import me.zilzu.mycoupon.application.service.CouponService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }


    //    {
//        "id": "Z4OV52SU",
//            "object": "coupon",
//            "amount_off": null,
//            "created": 1658661549,
//            "currency": "usd",
//            "duration": "repeating",
//            "duration_in_months": 3,
//            "livemode": false,
//            "max_redemptions": null,
    //        "name": "25.5% off",
//            "percent_off": 25.5,
//            "valid": true
//    }
    @GetMapping("api/v1/coupons/{id}")
    public CouponRetrieveResultResponse retrieveCoupon(@PathVariable String id) {

        CouponRetrieveResult couponRetrieveResult = couponService.retrieve(id);

        CouponRetrieveResultResponse couponRetrieveResultResponse = new CouponRetrieveResultResponse(
                couponRetrieveResult.id, couponRetrieveResult.object, couponRetrieveResult.amountOff, couponRetrieveResult.created, couponRetrieveResult.currency, couponRetrieveResult.duration, couponRetrieveResult.durationInMonths, couponRetrieveResult.livemode, couponRetrieveResult.maxRedemptions, couponRetrieveResult.name, couponRetrieveResult.percentOff, couponRetrieveResult.valid);

        return couponRetrieveResultResponse;
    }



}
