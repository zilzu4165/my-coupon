package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponDiscountAmountCalculator;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.SortingOrder;
import me.zilzu.mycoupon.storage.CouponEntity;
import me.zilzu.mycoupon.storage.NewCouponRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
public class CouponService {
    // 접근제어자는 가능한 좁게 만든다. 'private 부터 !' 좁은 상태 -> 넓은 상태 o , 넓은 상태 -> 좁은 상태 x
    // autowired 는 테스트 코드 x
    // final : 변수에 값이 반드시 한번 할당이 되어야한다.
    private final CouponIdGenerator couponIdGenerator;
    private final CouponDiscountAmountCalculator couponDiscountAmountCalculator;
    private final CouponValidator couponValidator;
    private final CouponHistoryRecorder couponHistoryRecorder;
    private final NewCouponRepository newCouponRepository;

    public CouponService(CouponIdGenerator couponIdGenerator,
                         CouponDiscountAmountCalculator couponDiscountAmountCalculator, CouponValidator couponValidator,
                         CouponHistoryRecorder couponHistoryRecorder, NewCouponRepository newCouponRepository) {
        this.couponIdGenerator = couponIdGenerator;
        this.couponDiscountAmountCalculator = couponDiscountAmountCalculator;
        this.couponValidator = couponValidator;
        this.couponHistoryRecorder = couponHistoryRecorder;
        this.newCouponRepository = newCouponRepository;
    }

    @Cacheable(value = "Coupon", key = "#id")
    public Coupon retrieve(CouponId id) {
        Optional<CouponEntity> foundCouponEntity = newCouponRepository.findById(id.value);

        if (foundCouponEntity.isEmpty()) {
            throw new RuntimeException("coupon id : " + id + "조회결과가 없습니다.");
        }

        CouponEntity entity = foundCouponEntity.get();
        return new Coupon(new CouponId(entity.id), entity.duration, entity.durationInMonth, entity.couponCurrency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime);
    }

    public Page<Coupon> retrieveList(Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdTime");
        Page<CouponEntity> foundAll = newCouponRepository.findAll(pageRequest);
        Page<Coupon> coupons = foundAll.map(couponEntity -> new Coupon(new CouponId(couponEntity.id), couponEntity.duration, couponEntity.durationInMonth, couponEntity.couponCurrency, couponEntity.discountType, couponEntity.amountOff, couponEntity.percentOff, couponEntity.valid, couponEntity.createdTime));

        return coupons;
    }

    public Coupon create(CouponCreationRequest couponCreationRequest) {
        String couponId = couponIdGenerator.generate();

        couponValidator.validate(couponCreationRequest);

        CouponEntity entity = new CouponEntity(couponId, couponCreationRequest.duration, couponCreationRequest.durationInMonths, couponCreationRequest.couponCurrency, couponCreationRequest.discountType, couponCreationRequest.amountOff, couponCreationRequest.percentOff, true, LocalDateTime.now());
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
    public CouponApplicationResult apply(CouponId id, Double price) {
        Coupon foundCoupon = retrieve(id);

        if (!foundCoupon.valid) {
            throw notUsableCouponException();
        }

        if (foundCoupon.duration == CouponDuration.ONCE) {
            CouponEntity entity = newCouponRepository.findById(foundCoupon.id.value).get();
            entity.valid = false;
        }
        BigDecimal discountedPrice = couponDiscountAmountCalculator.getDiscountedPrice(foundCoupon, price);

        CouponHistory history = couponHistoryRecorder.record(foundCoupon, price, discountedPrice);

        return new CouponApplicationResult(id, history.usageTime);
    }


    private static RuntimeException notUsableCouponException() {
        throw new RuntimeException("사용할 수 없는 쿠폰입니다.");
    }
}
