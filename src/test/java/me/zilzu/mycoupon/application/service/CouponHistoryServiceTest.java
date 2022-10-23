package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponHistoryId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponHistoryServiceTest {
    @Autowired
    CouponService couponService;
    @Autowired
    CouponHistoryService couponHistoryService;
    @Autowired
    CouponDiscountAmountCalculator couponDiscountAmountCalculator;

    @Test
    @DisplayName("쿠폰 적용 고도화: 물건의 가격을 입력받아 할인적용한 금액을 추가로 저장한다")
    void coupon_apply_with_discountPrice() {
        List<Coupon> createCouponList = createOnceCouponDatas();
        Coupon coupon01 = createCouponList.get(0);
        Coupon coupon02 = createCouponList.get(1);
        Coupon coupon03 = createCouponList.get(2);

        Double price = 175.5; //USD
        assertThat(couponDiscountAmountCalculator.calculate(coupon01, price)).isEqualTo(70L);
        assertThat(couponDiscountAmountCalculator.calculate(coupon02, price)).isEqualTo(65.19825);
        assertThat(couponDiscountAmountCalculator.calculate(coupon03, price)).isEqualTo(price);

    }

    @Test
    @DisplayName("쿠폰 할인 가격을 포함하여 저장한 값이 사용이력 DB에 저장되었는지 확인한다.")
    void evaluate_couponHistory_data_in_DB() {
        List<Coupon> createCouponList = createOnceCouponDatas();
        Double price = 217.75;
        createCouponList.stream()
                .map(coupon -> couponService.saveCouponHistory(coupon, price, couponDiscountAmountCalculator.calculate(coupon, price)))
                .map(couponHistory -> new CouponHistoryId(couponHistory.id))
                .map(couponHistoryService::retrieveCouponHistory)
                .forEach(couponHistory -> assertThat(couponHistory).isNotNull());
        //findAll로 테스트 하는 것은 프로젝트 통합테스트 시 오류가 발생!
        //assertThat(couponUsageHistoryRepository.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("일회용 쿠폰 사용 후 상태 값이 사용 불가로 변경되었는지 확인한다")
    void evaluate_used_coupon_status() {
        CouponCreationRequest requestAmount = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 70L, null);
        Coupon coupon = couponService.create(requestAmount);
        Double price = 175.15;
        CouponApplicationResult apply = couponService.apply(coupon.id, price);
        Coupon usedCoupon = couponService.retrieve(apply.couponId);

        assertThat(usedCoupon.valid).isFalse();
    }

    @Test
    @DisplayName("반복 가능한 쿠폰 사용시 쿠폰 사용이력에 해당 쿠폰id로 n 건수가 저장되는지 확인")
    void repeatable_coupon_apply_history() {
        Double price = 255.5;
        CouponCreationRequest requestRepeat = new CouponCreationRequest(CouponDuration.REPEATING, 1, DiscountType.AMOUNT, 25L, null);
        Coupon repeatableCoupon = couponService.create(requestRepeat);
        couponService.apply(repeatableCoupon.id, price);
        couponService.apply(repeatableCoupon.id, price);
        couponService.apply(repeatableCoupon.id, price);
        List<CouponHistory> sameCouponHistories = couponHistoryService.retrieveCouponHistoryList(repeatableCoupon.id);
        assertThat(sameCouponHistories).hasSize(3);
        Set<String> historyIdSet = sameCouponHistories.stream().map(couponHistory -> couponHistory.id).collect(Collectors.toSet());
        assertThat(historyIdSet).hasSize(3);
    }

    private List<Coupon> createOnceCouponDatas() {
        List<Coupon> list = new ArrayList<>();
        CouponCreationRequest requestAmount = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 70L, null);
        CouponCreationRequest requestPercent = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.PERCENTAGE, null, 37.15);
        CouponCreationRequest requestAmountOver = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 700L, null);
        list.add(couponService.create(requestAmount));
        list.add(couponService.create(requestPercent));
        list.add(couponService.create(requestAmountOver));
        return list;
    }
}