package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;
import me.zilzu.mycoupon.common.enums.SortingOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class CouponCreateTest {

    @Autowired
    CouponService couponService;

    @AfterEach
    void emptyCoupon() {
        couponService.emptyCoupon();
    }

    @Test
    @DisplayName("쿠폰 100개가 동시에 생성됐을 때, 100개가 정상적으로 생성된다. ")
    void test1() throws InterruptedException {
        createCoupons(100, 100);
        assertThat(couponService.getAllCouponSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다. -1")
    void testRunnable() throws InterruptedException {
        createCoupons(10000, 100);
        // Test Worker Thread
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
        assertThat(couponService.getAllCouponSize()).isEqualTo(10000);
    }


    @Test
    @DisplayName("쿠폰을 100개 생성 후, 가장 최근 생성된 10개의 쿠폰 리스트를 내림차순으로 반환한다")
    void test3() throws InterruptedException {
        // 최근 생성된 10개 쿠폰 리스트 내림차순 반환
        createCoupons(100, 100);
        List<Coupon> coupons = couponService.findRecentlyCreatedCoupon(10);

        assertThat(coupons.size()).isEqualTo(10);
        assertThat(coupons).isSortedAccordingTo(
                Comparator.comparing((Function<Coupon, LocalDateTime>) coupon -> coupon.createdTime).reversed()
        ); // 내림차순 정렬인지 확인
    }

    @Test
    @DisplayName("쿠폰을 n개 생성 후, 가장 최근 생성된 m개의 쿠폰 리스트를 오름차순으로 반환한다")
    void test4() throws InterruptedException {
        createCoupons(100, 100);
        List<Coupon> coupons = couponService.findRecentlyCreatedCoupon(10, SortingOrder.ASC);

        assertThat(coupons.size()).isEqualTo(10);
        assertThat(coupons).isSortedAccordingTo(Comparator.comparing(coupon -> coupon.createdTime));  // 내림차순 정렬인지 확인
    }

    @Test
    @DisplayName("쿠폰을 n개 생성 후, 가장 최근 생성된 m개의 쿠폰 리스트를 내림차순으로 반환한다")
    void test5() throws InterruptedException {
        createCoupons(100);
        // 최근 생성된 10개 쿠폰 리스트 오른차순 또는 내림차순 반환
        List<Coupon> coupons = couponService.findRecentlyCreatedCoupon(10, SortingOrder.DESC);

        assertThat(coupons.size()).isEqualTo(10);
        assertThat(coupons).isSortedAccordingTo(
                Comparator.comparing((Function<Coupon, LocalDateTime>) coupon -> coupon.createdTime).reversed()
        );  // 내림차순 정렬인지 확인
    }

    @DisplayName("생성한 coupon을 조회했을 때, 유저가 정한 통화로 조회가 된다.")
    @ParameterizedTest
    @EnumSource(value = CouponCurrency.class)
    void test6(CouponCurrency couponCurrency) {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, null, null, null);

        Coupon coupon = couponService.createWithCurrency(couponCreationRequest, couponCurrency);
        Coupon foundCoupon = couponService.retrieve(coupon.id);

        assertThat(foundCoupon.id).isEqualTo(coupon.id);
        assertThat(foundCoupon.couponCurrency).isEqualTo(couponCurrency);
    }

    @DisplayName("생성한 coupon을 조회했을 때, 유저가 정한 통화로 조회가 된다. 기본 통화는 USD이다.")
    @Test
    void test7() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, null, null, null);

        Coupon coupon = couponService.createWithCurrency(couponCreationRequest, CouponCurrency.USD);
        Coupon foundCoupon = couponService.retrieve(coupon.id);

        assertThat(foundCoupon.id).isEqualTo(coupon.id);
        assertThat(foundCoupon.couponCurrency).isEqualTo(CouponCurrency.USD);
    }

    @DisplayName("쿠폰을 생성 할 때 duration이 {ONCE, FOREVER} 유형은 durationInMonths가 값이 존재할 수 없다.")
    @ParameterizedTest
    @EnumSource(value = CouponDuration.class, names = {"ONCE", "FOREVER"})
    void test9(CouponDuration duration) {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(duration, null, null, null, null);
        Coupon coupon = couponService.create(couponCreationRequest);

        Coupon retrieve = couponService.retrieve(coupon.id);

        assertThat(retrieve.duration).isEqualTo(coupon.duration);
        assertThat(retrieve.durationInMonth).isNull();
    }

    @DisplayName("REPEATING 유형이 아닌데 durationInMonths 값이 들어오면 IllegalArgumentException 발생시킨다. ")
    @ParameterizedTest
    @EnumSource(value = CouponDuration.class, names = {"ONCE", "FOREVER"})
    void test10(CouponDuration duration) {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(duration, 0, null, null, null);

        assertThatThrownBy(() -> {
            couponService.create(couponCreationRequest);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("REPEATING 유형의 경우 durationInMonth에 대한 정보가 담겨 있어야 한다.")
    @Test
    void test11() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, 3, null, null, null);
        Coupon coupon = couponService.create(couponCreationRequest);

        Coupon retrieve = couponService.retrieve(coupon.id);

        assertThat(retrieve.duration).isEqualTo(coupon.duration);
        assertThat(retrieve.durationInMonth).isNotNull();
    }

    @DisplayName("쿠폰 생성시 DiscountType이 AMOUNT 일 때, amountOff 에 값이 존재해야한다.")
    @Test
    void test12() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.AMOUNT, 1000L, null);
        Coupon coupon = couponService.create(couponCreationRequest);

        Coupon retrieve = couponService.retrieve(coupon.id);

        assertThat(retrieve.discountType).isEqualTo(coupon.discountType);
        assertThat(retrieve.amountOff).isEqualTo(coupon.amountOff);
        assertThat(retrieve.percentOff).isEqualTo(coupon.percentOff);
    }

    @DisplayName("쿠폰 생성시 DiscountType이 AMOUNT 일 때, percentOff 에 값이 존재하면 IllegalArgumentException을 발생시킨다.")
    @Test
    void test13() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.AMOUNT, 1000L, 1000.0);

        assertThatThrownBy(() ->
                couponService.create(couponCreationRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("쿠폰 생성시 DiscountType이 PERCENTAGE 일 때, percentOff 에 값이 존재해야한다.")
    @Test
    void test14() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.PERCENTAGE, null, 1000.0);
        Coupon coupon = couponService.create(couponCreationRequest);

        Coupon retrieve = couponService.retrieve(coupon.id);

        assertThat(retrieve.discountType).isEqualTo(coupon.discountType);
        assertThat(retrieve.amountOff).isEqualTo(coupon.amountOff);
        assertThat(retrieve.percentOff).isEqualTo(coupon.percentOff);
    }

    @DisplayName("쿠폰 생성시 DiscountType이 PERCENTAGE라면 amountOff 에 값이 존재하면 IllegaArgumentException을 발생시킨다.")
    @Test
    void test15() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.PERCENTAGE, 1000L, 1000.0);

        assertThatThrownBy(() ->
                couponService.create(couponCreationRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("쿠폰 생성시 amountOff , percentOff 두 값이 동시에 존재하면 IllegalArgumentException 을 발생시킨다.")
    @Test
    void test16() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.REPEATING, 3, DiscountType.AMOUNT, 1000L, 1000.0);

        assertThatThrownBy(() -> {
            couponService.create(couponCreationRequest);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("쿠폰 생성시 valid 는 true로 생성되어야 한다.")
    @Test
    void test17() {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, 1000L, null);

        Coupon coupon = couponService.create(couponCreationRequest);
        Coupon foundCoupon = couponService.retrieve(coupon.id);

        assertThat(coupon.valid).isTrue();
        assertThat(foundCoupon.valid).isTrue();
    }

    private void createCoupons(int count, int nThreads) throws InterruptedException {

        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, null, null, null);

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads); // threadPoolSize 100

        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                couponService.create(couponCreationRequest);
            });
        }
        executorService.shutdown();
        Thread.sleep(1000);
    }

    private void createCoupons(int count) throws InterruptedException {
        createCoupons(count, 100);
    }
}
