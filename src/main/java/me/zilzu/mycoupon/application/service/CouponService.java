package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.SortingOrder;
import me.zilzu.mycoupon.storage.CouponEntity;
import me.zilzu.mycoupon.storage.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {
    // 접근제어자는 가능한 좁게 만든다. 'private 부터 !' 좁은 상태 -> 넓은 상태 o , 넓은 상태 -> 좁은 상태 x
    // autowired 는 테스트 코드 x
    private final CouponRepository couponRepository; // final : 변수에 값이 반드시 한번 할당이 되어야한다.
    // final : 변수에 값이 반드시 한번 할당이 되어야한다.
    private final CouponIdGenerator couponIdGenerator;
    private final CouponValidator couponValidator;
    private final CouponHistoryRecorder couponHistoryRecorder;

    public CouponService(CouponRepository couponRepository,
                         CouponIdGenerator couponIdGenerator,
                         CouponValidator couponValidator,
                         CouponHistoryRecorder couponHistoryRecorder) {
        this.couponRepository = couponRepository;
        this.couponIdGenerator = couponIdGenerator;
        this.couponValidator = couponValidator;
        this.couponHistoryRecorder = couponHistoryRecorder;
    }

    public Coupon retrieve(String id) {
        CouponEntity entity = couponRepository.retrieve(id);
        return new Coupon(entity.id, entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime);
    }

    public List<Coupon> retrieveList(Integer limit) {
        List<Coupon> coupons = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            coupons.add(new Coupon("Z4OV52SU", null, null, null, null, null, null, null, LocalDateTime.now()));
        }
        return coupons;
    }

    public Coupon create(CouponRequest couponRequest) {
        return createWithCurrency(couponRequest, CouponCurrency.USD);
    }

    public Coupon createWithCurrency(CouponRequest couponRequest, CouponCurrency couponCurrency) {
        String couponId = couponIdGenerator.generate();

        couponValidator.validate(couponRequest);

        CouponEntity entity = new CouponEntity(couponId, couponRequest.duration, couponRequest.durationInMonths, couponCurrency, couponRequest.discountType, couponRequest.amountOff, couponRequest.percentOff, true, LocalDateTime.now());
        couponRepository.save(entity);

        return new Coupon(entity.id, entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime);
    }

    public CouponDeleteResult delete(String id) {
        CouponEntity deletedCouponEntity = couponRepository.delete(id);
        return new CouponDeleteResult(deletedCouponEntity.id);

    }

    public Long getAllCouponSize() {
        return couponRepository.getAllCouponSize();
    }

    public void emptyCoupon() {
        couponRepository.emptyCoupon();
    }

    public List<Coupon> findRecentlyCreatedCoupon(Integer limit) {
        return findRecentlyCreatedCoupon(limit, SortingOrder.DESC);
    }

    public List<Coupon> findRecentlyCreatedCoupon(Integer limit, SortingOrder sortedBy) {
        List<CouponEntity> couponEntities = couponRepository.selectRecently(limit, sortedBy);

        return couponEntities.stream()
                .map(entity -> new Coupon(entity.id, entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime))
                .collect(Collectors.toList());
    }

    public CouponApplicationResult apply(String couponId) {
        Coupon foundCoupon = retrieve(couponId);

        if (!foundCoupon.valid) {
            throw notUsableCouponException();
        }

        if (foundCoupon.duration == CouponDuration.ONCE) {
            couponRepository.invalidate(foundCoupon.id);
        }

        CouponHistory history = couponHistoryRecorder.record(foundCoupon.id);

        return new CouponApplicationResult(couponId, history.usageTime);
    }

    private static RuntimeException notUsableCouponException() {
        return new RuntimeException("사용할 수 없는 쿠폰입니다.");
    }
}
