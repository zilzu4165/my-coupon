package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.SortingOrder;
import me.zilzu.mycoupon.storage.CouponEntity;
import me.zilzu.mycoupon.storage.CouponHistoryRepository;
import me.zilzu.mycoupon.storage.CouponRepository;
import me.zilzu.mycoupon.storage.CouponUsageHistoryEntity;
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
    private final CouponHistoryRepository couponHistoryRepository; // final : 변수에 값이 반드시 한번 할당이 되어야한다.
    private final CouponIdGenerate couponIdGenerate;
    private final CouponValidator couponValidator;

    public CouponService(CouponRepository couponRepository,
                         CouponHistoryRepository couponHistoryRepository,
                         CouponIdGenerate couponIdGenerate,
                         CouponValidator couponValidator) {
        this.couponRepository = couponRepository;
        this.couponHistoryRepository = couponHistoryRepository;
        this.couponIdGenerate = couponIdGenerate;
        this.couponValidator = couponValidator;
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

    public CouponHistory apply(String couponId) {
        Coupon foundCoupon = retrieve(couponId);

        if (!foundCoupon.valid) {
            throw new RuntimeException("사용할 수 없는 쿠폰입니다.");
        }
        if (foundCoupon.duration == CouponDuration.ONCE) {
            couponRepository.invalidate(foundCoupon.id);
        }

        CouponUsageHistoryEntity historyEntity = new CouponUsageHistoryEntity(couponIdGenerate.generate(), foundCoupon.id, LocalDateTime.now());

        couponHistoryRepository.save(historyEntity);

        return new CouponHistory(historyEntity.id, historyEntity.refCouponId, historyEntity.usageTime);
    }

    public CouponHistory retrieveCouponHistory(String historyId) {
        CouponUsageHistoryEntity historyEntity = couponHistoryRepository.selectCouponUsageHistory(historyId);
        return new CouponHistory(historyEntity.id, historyEntity.refCouponId, historyEntity.usageTime);
    }
}
