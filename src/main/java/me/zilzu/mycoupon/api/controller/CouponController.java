package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.application.service.CouponCreationRequest;
import me.zilzu.mycoupon.application.service.CouponDeleteResult;
import me.zilzu.mycoupon.application.service.CouponService;
import me.zilzu.mycoupon.common.CouponId;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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
    public CouponRetrieveListResponse retrieveListCoupons(HttpServletRequest request,
                                                          @RequestParam Integer limit) {

        String requestURI = request.getRequestURI();

        List<Coupon> coupons = couponService.retrieveList(limit);

        List<CouponRetrieveResultResponse> couponListResults = coupons
                .stream()
                .map(CouponRetrieveResultResponse::new)
                .collect(Collectors.toList());
        return new CouponRetrieveListResponse("list", requestURI, false, couponListResults);
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