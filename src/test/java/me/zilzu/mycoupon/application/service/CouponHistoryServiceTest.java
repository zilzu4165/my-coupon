package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponHistoryId;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import me.zilzu.mycoupon.storage.CouponUsageHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    CouponUsageHistoryRepository couponUsageHistoryRepository;
    @Autowired
    CouponDiscountAmountCalculator couponDiscountAmountCalculator;

    @Test
    @DisplayName("쿠폰 적용 고도화: 물건의 가격을 입력받아 할인적용한 금액을 추가로 저장한다")
    void coupon_apply_with_discountPrice() {
        CouponCreationRequest requestAmount = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 70L, null);
        CouponCreationRequest requestPercent = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.PERCENTAGE, null, 37.15);
        CouponCreationRequest requestAmountOver = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 700L, null);
        Coupon coupon01 = couponService.create(requestAmount);
        Coupon coupon02 = couponService.create(requestPercent);
        Coupon coupon03 = couponService.create(requestAmountOver);

        couponService.consumeCoupon(coupon01);
        couponService.consumeCoupon(coupon02);
        couponService.consumeCoupon(coupon03);

        System.out.println("==== evaluate discounted price");
        Double price = 175.5; //USD
        CouponHistory applyAmount = couponService.saveCouponHistory(coupon01.id, price,
                couponDiscountAmountCalculator.calculate(coupon01, price));
        CouponHistory applyPercent = couponService.saveCouponHistory(coupon02.id, price,
                couponDiscountAmountCalculator.calculate(coupon02, price));
        CouponHistory applyAmountOver = couponService.saveCouponHistory(coupon03.id, price,
                couponDiscountAmountCalculator.calculate(coupon03, price));
        assertThat(applyAmount.discountedPrice).isEqualTo(70L);
        assertThat(applyPercent.discountedPrice).isEqualTo(65.19825);
        assertThat(applyAmountOver.discountedPrice).isEqualTo(price);

        System.out.println("==== evaluate used coupon status");
        Coupon used01 = couponService.retrieve(new CouponId(coupon01.id.value));
        Coupon used02 = couponService.retrieve(new CouponId(coupon02.id.value));
        Coupon used03 = couponService.retrieve(new CouponId(coupon03.id.value));
        assertThat(used01.valid).isFalse();
        assertThat(used02.valid).isFalse();
        assertThat(used03.valid).isFalse();

        System.out.println("==== evaluate couponHistory data in DB");
        CouponHistory result01 = couponHistoryService.retrieveCouponHistory(new CouponHistoryId(applyAmount.id));
        CouponHistory result02 = couponHistoryService.retrieveCouponHistory(new CouponHistoryId(applyPercent.id));
        CouponHistory result03 = couponHistoryService.retrieveCouponHistory(new CouponHistoryId(applyAmountOver.id));
        assertThat(result01).isNotNull();
        assertThat(result02).isNotNull();
        assertThat(result03).isNotNull();
        //findAll로 테스트 하는 것은 프로젝트 통합테스트 시 오류가 발생!
        //assertThat(couponUsageHistoryRepository.findAll()).hasSize(3);

        System.out.println("==== evaluate repeatable coupon data in DB");
        CouponCreationRequest requestRepeat = new CouponCreationRequest(CouponDuration.REPEATING, 1, DiscountType.AMOUNT, 25L, null);
        Coupon coupon04 = couponService.create(requestRepeat);
        couponService.apply(coupon04.id, price);
        couponService.apply(coupon04.id, price);
        couponService.apply(coupon04.id, price);
        List<CouponHistory> sameCouponHistories = couponHistoryService.retrieveCouponHistoryList(coupon04.id);
        assertThat(sameCouponHistories).hasSize(3);
        Set<String> historyIdSet = sameCouponHistories.stream().map(couponHistory -> couponHistory.id).collect(Collectors.toSet());
        assertThat(historyIdSet).hasSize(3);
    }
}