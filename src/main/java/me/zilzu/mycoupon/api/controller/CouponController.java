package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.application.service.CouponService;
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

        Coupon coupon = couponService.retrieve(id);

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
    public CouponCreatedResponse createCoupons(@RequestBody CouponRequest couponRequest) {

        Coupon coupon = couponService.create(couponRequest);

        return new CouponCreatedResponse(coupon);
    }

}
