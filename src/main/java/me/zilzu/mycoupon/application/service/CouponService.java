package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.SortingOrder;
import me.zilzu.mycoupon.storage.CouponEntity;
import me.zilzu.mycoupon.storage.NewCouponRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
public class CouponService {
    // 접근제어자는 가능한 좁게 만든다. 'private 부터 !' 좁은 상태 -> 넓은 상태 o , 넓은 상태 -> 좁은 상태 x
    // autowired 는 테스트 코드 x
    // final : 변수에 값이 반드시 한번 할당이 되어야한다.
    private final CouponIdGenerator couponIdGenerator;
    private final CouponValidator couponValidator;
    private final CouponHistoryRecorder couponHistoryRecorder;
    private final NewCouponRepository newCouponRepository;

    public CouponService(CouponIdGenerator couponIdGenerator,
                         CouponValidator couponValidator,
                         CouponHistoryRecorder couponHistoryRecorder, NewCouponRepository newCouponRepository) {
        this.couponIdGenerator = couponIdGenerator;
        this.couponValidator = couponValidator;
        this.couponHistoryRecorder = couponHistoryRecorder;
        this.newCouponRepository = newCouponRepository;
    }

    public Coupon retrieve(CouponId id) {
        CouponEntity entity = newCouponRepository.findById(id.value).get();
        return new Coupon(new CouponId(entity.id), entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime);
    }

    public List<Coupon> retrieveList(Integer limit) {
        List<Coupon> coupons = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            coupons.add(new Coupon(new CouponId("Z4OV52SU"), null, null, null, null, null, null, null, LocalDateTime.now()));
        }
        return coupons;
    }

    public Coupon create(CouponCreationRequest couponCreationRequest) {
        return createWithCurrency(couponCreationRequest, CouponCurrency.USD);
    }

    public Coupon createWithCurrency(CouponCreationRequest couponCreationRequest, CouponCurrency couponCurrency) {
        String couponId = couponIdGenerator.generate();

        couponValidator.validate(couponCreationRequest);

        CouponEntity entity = new CouponEntity(couponId, couponCreationRequest.duration, couponCreationRequest.durationInMonths, couponCurrency, couponCreationRequest.discountType, couponCreationRequest.amountOff, couponCreationRequest.percentOff, true, LocalDateTime.now());
        newCouponRepository.save(entity);

        return new Coupon(new CouponId(entity.id), entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime);
    }

    public CouponDeleteResult delete(CouponId id) {
        newCouponRepository.deleteById(id.value);
        return new CouponDeleteResult(id);
    }

    public Long getAllCouponSize() {
        return newCouponRepository.count();
    }

    public void emptyCoupon() {
        newCouponRepository.deleteAll();
    }

    public List<Coupon> findRecentlyCreatedCoupon(Integer limit) {
        return findRecentlyCreatedCoupon(limit, SortingOrder.DESC);
    }

    public List<Coupon> findRecentlyCreatedCoupon(Integer limit, SortingOrder sortedBy) {
        List<CouponEntity> couponEntities = newCouponRepository.findAll();
        List<CouponEntity> sortedCoupons = new ArrayList<>();

        if (sortedBy == SortingOrder.ASC) {
            sortedCoupons = couponEntities
                    .stream()
                    .sorted(comparing(entity1 -> entity1.createdTime))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if (sortedBy == SortingOrder.DESC) {
            sortedCoupons = couponEntities
                    .stream()
                    .sorted(comparing((Function<CouponEntity, LocalDateTime>) couponEntity -> couponEntity.createdTime)
                            .reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return sortedCoupons.stream()
                .map(entity -> new Coupon(new CouponId(entity.id), entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime))
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponApplicationResult apply(CouponId id) {
        Coupon foundCoupon = retrieve(id);

        if (!foundCoupon.valid) {
            throw notUsableCouponException();
        }

        if (foundCoupon.duration == CouponDuration.ONCE) {
            CouponEntity entity = newCouponRepository.findById(foundCoupon.id.value).get();
            entity.valid = false;
        }

        CouponHistory history = couponHistoryRecorder.record(foundCoupon.id);

        return new CouponApplicationResult(id, history.usageTime);
    }

    private static RuntimeException notUsableCouponException() {
        throw new RuntimeException("사용할 수 없는 쿠폰입니다.");
    }
}
