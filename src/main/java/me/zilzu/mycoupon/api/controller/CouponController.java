package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.application.service.CouponCreationRequest;
import me.zilzu.mycoupon.application.service.CouponDeleteResult;
import me.zilzu.mycoupon.application.service.CouponService;
import me.zilzu.mycoupon.common.CouponId;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
        Coupon coupon = couponService.create(new CouponCreationRequest(couponRequestDto.duration, couponRequestDto.durationInMonths, couponRequestDto.discountType, couponRequestDto.amountOff, couponRequestDto.percentOff));

        return new CouponCreatedResponse(coupon);
    }

    @DeleteMapping("/api/v1/coupons/{id}")
    public CouponDeletedResponse deleteCoupon(@PathVariable String id) {

        CouponDeleteResult deleteResult = couponService.delete(new CouponId(id));

        return new CouponDeletedResponse(deleteResult.deletedCouponId.value);
    }


    @PostMapping("/api/v1/coupons/{id}/apply")
    public String applyCoupon(@PathVariable String id) {
        couponService.apply(new CouponId(id));
        return "쿠폰을 적용했습니다.";
    }

}