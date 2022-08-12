package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.comparing;

@Service
public class CouponService {
    // 접근제어자는 가능한 좁게 만든다. 'private 부터 !' 좁은 상태 -> 넓은 상태 o , 넓은 상태 -> 좁은 상태 x
    // autowired 는 테스트 코드 x
    private final CouponRepository couponRepository; // final : 변수에 값이 반드시 한번 할당이 되어야한다.
    private final CouponIdGenerate couponIdGenerate;

    public CouponService(CouponRepository couponRepository,
                         CouponIdGenerate couponIdGenerate) {
        this.couponRepository = couponRepository;
        this.couponIdGenerate = couponIdGenerate;
    }

    public Coupon retrieve(String id) {
        return couponRepository.retrieve(id);
    }

    public List<Coupon> retrieveList(Integer limit) {
        List<Coupon> coupons = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            coupons.add(new Coupon("Z4OV52SU", "coupon", null, System.currentTimeMillis() / 1000, "usd",
                    "repeating", 3, false,
                    null, "25.5% off", 25.5F, true, LocalDateTime.now()));
        }
        return coupons;
    }

    public Coupon create(CouponRequest couponRequest) {

        String couponId = couponIdGenerate.generate();
        Coupon coupon = new Coupon(couponId, "coupon", null, System.currentTimeMillis() / 1000, "usd",
                couponRequest.getDuration(), couponRequest.getDurationInMonths(), false,
                null, "25.5% off", 25.5F, true, LocalDateTime.now());

        couponRepository.save(coupon);
        return coupon;
    }

    public CouponDeleteResult delete(String id) {
        return new CouponDeleteResult(id, "coupon", true);
    }

    public Long getAllCouponSize() {
        return couponRepository.getAllCouponSize();
    }

    public void emptyCoupon() {
        couponRepository.emptyCoupon();
    }

    public List<Coupon> findRecentlyCreatedCoupon(Integer limit) {
        return couponRepository.selectRecently(limit);
    }
}
