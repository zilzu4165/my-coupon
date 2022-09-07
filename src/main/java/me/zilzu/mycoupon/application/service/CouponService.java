package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
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
    private final CouponIdGenerate couponIdGenerate;

    public CouponService(CouponRepository couponRepository,
                         CouponIdGenerate couponIdGenerate) {
        this.couponRepository = couponRepository;
        this.couponIdGenerate = couponIdGenerate;
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
        String couponId = couponIdGenerate.generate();

        couponCreateValidate(couponRequest);

        CouponEntity entity = new CouponEntity(couponId, couponRequest.duration, couponRequest.durationInMonths, couponCurrency, couponRequest.discountType, couponRequest.amountOff, couponRequest.percentOff, true, LocalDateTime.now());
        couponRepository.save(entity);

        return new Coupon(entity.id, entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime);
    }

    private static void couponCreateValidate(CouponRequest couponRequest) {
        if (couponRequest.duration != CouponDuration.REPEATING && couponRequest.durationInMonths != null) {
            throw new IllegalArgumentException("duration이 REPEATING 유형이 아니라면 durationInMonths 값을 가질 수 없습니다");
        }
        if (couponRequest.amountOff != null && couponRequest.percentOff != null) {
            throw new IllegalArgumentException("금액할인과 비율할인이 동시에 값을 가질 수 없습니다.");
        }
        if (couponRequest.discountType == DiscountType.AMOUNT && couponRequest.amountOff == null) {
            throw new IllegalArgumentException("discountType이 AMOUNT일 경우 amountOff 에 값이 존재해야 합니다.");
        }
        if (couponRequest.discountType == DiscountType.AMOUNT && couponRequest.percentOff != null) {
            throw new IllegalArgumentException("discountType이 AMOUNT일 경우 percentOff 에 값이 존재할 수 없습니다.");
        }
        if (couponRequest.discountType == DiscountType.PERCENTAGE && couponRequest.percentOff == null) {
            throw new IllegalArgumentException("discountType이 PERCENTAGE일 경우 percentOff 에 값이 존재해야 합니다.");
        }
        if (couponRequest.discountType == DiscountType.PERCENTAGE && couponRequest.amountOff != null) {
            throw new IllegalArgumentException("discountType이 PERCENTAGE일 경우 amountOff 에 값이 존재할 수 없습니다.");
        }
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

    public String apply(String id) {
        Coupon foundCoupon = retrieve(id);

        if (!foundCoupon.valid) {
            throw new RuntimeException("사용할 수 없는 쿠폰입니다.");
        }
        // CouponDuration이 ONCE 유형이면 사용후 valid false
        if (foundCoupon.duration == CouponDuration.ONCE) {
            CouponEntity entity = new CouponEntity(foundCoupon.id, foundCoupon.duration, foundCoupon.durationInMonth, foundCoupon.couponCurrency,
                    foundCoupon.discountType, foundCoupon.amountOff, foundCoupon.percentOff, false, foundCoupon.createdTime);

            couponRepository.applyCoupon(entity);
        }
        return "사용됨";
    }
}
