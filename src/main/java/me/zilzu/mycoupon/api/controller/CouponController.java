package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.*;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;

@RestController
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/api/v1/coupons/{id}")
    public CouponRetrieveResultResponse retrieveCoupon(@PathVariable String id) {

        CouponId couponId = new CouponId(id);

        Coupon coupon = couponService.retrieve(couponId);

        return new CouponRetrieveResultResponse(coupon);
    }

    @GetMapping("/api/v1/coupons")
    public Page<CouponRetrieveResultResponse> retrieveListCoupons(@RequestParam Integer page, @RequestParam Integer pageSize) {
        Page<Coupon> coupons = couponService.retrieveList(page, pageSize);
        Page<CouponRetrieveResultResponse> couponRetrieveResultResponsePage = coupons.map(coupon -> new CouponRetrieveResultResponse(coupon));

        return couponRetrieveResultResponsePage;
    }

    @PostMapping("/api/v1/coupons")
    public CouponCreatedResponse createCoupons(@RequestBody CouponRequestDto couponRequestDto) {
        Coupon coupon = couponService.create(new CouponCreationRequest(couponRequestDto.duration, couponRequestDto.durationInMonths, couponRequestDto.discountType, couponRequestDto.currency, couponRequestDto.amountOff, couponRequestDto.percentOff), LocalDateTime.now());

        return new CouponCreatedResponse(coupon);
    }

    @DeleteMapping("/api/v1/coupons/{id}")
    public CouponDeletedResponse deleteCoupon(@PathVariable String id) {

        CouponDeleteResult deleteResult = couponService.delete(new CouponId(id));

        return new CouponDeletedResponse(deleteResult.deletedCouponId.value);
    }


    @PostMapping("/api/v1/coupons/{id}/apply")
    public String applyCoupon(@PathVariable String id) {
//        couponService.apply(new CouponId(id));
        return "쿠폰을 적용했습니다.";
    }

    @GetMapping("/api/v1/stats")
    public CouponRateStatsResponse analyseStatsCoupon(@RequestParam(value = "target") YearMonth yearMonth
            , @RequestParam(value = "currency") Currency currency) {

        CouponStatsResult results = couponService.analyseStatsOf(yearMonth, currency);

        return new CouponRateStatsResponse(results.sum, results.average, results.currency);

    }
}