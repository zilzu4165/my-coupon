package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.*;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
public class CouponController {

    private final CouponService couponService;

    private final CouponAnalyzeService couponAnalyzeService;

    public CouponController(
            CouponService couponService,
            CouponAnalyzeService couponAnalyzeService
    ) {
        this.couponService = couponService;
        this.couponAnalyzeService = couponAnalyzeService;
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
        Coupon coupon = couponService.create(new CouponCreationRequest(couponRequestDto.duration, couponRequestDto.durationInMonths, couponRequestDto.discountType, couponRequestDto.currency, couponRequestDto.amountOff, couponRequestDto.percentOff));

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
    public CouponAnalyzeResponse couponAnalyze(
            @RequestParam(value = "target") String targetYearMonth,
            @RequestParam(value = "currency") Currency currency
    ) {
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(targetYearMonth);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        CouponAnalyzeResult result = couponAnalyzeService.analyzeBy(yearMonth, currency);
        return new CouponAnalyzeResponse(result);
    }

}