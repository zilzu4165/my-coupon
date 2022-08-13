package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(coupons).isSortedAccordingTo(Comparator.comparing(Coupon::getDate).reversed()); // 내림차순 정렬인지 확인
    }

    @Test
    @DisplayName("쿠폰을 n개 생성 후, 가장 최근 생성된 m개의 쿠폰 리스트를 오름차순으로 반환한다")
    void test4() throws InterruptedException {
        createCoupons(100, 100);
        List<Coupon> coupons = couponService.findRecentlyCreatedCoupon(10, SortingOrder.ASC);

        assertThat(coupons.size()).isEqualTo(10);
        assertThat(coupons).isSortedAccordingTo(Comparator.comparing(Coupon::getDate));  // 내림차순 정렬인지 확인
    }

    @Test
    @DisplayName("쿠폰을 n개 생성 후, 가장 최근 생성된 m개의 쿠폰 리스트를 내림차순으로 반환한다")
    void test5() throws InterruptedException {
        createCoupons(100);
        // 최근 생성된 10개 쿠폰 리스트 오른차순 또는 내림차순 반환
        List<Coupon> coupons = couponService.findRecentlyCreatedCoupon(10, SortingOrder.DESC);

        assertThat(coupons.size()).isEqualTo(10);
        assertThat(coupons).isSortedAccordingTo(Comparator.comparing(Coupon::getDate).reversed());  // 내림차순 정렬인지 확인
    }

    private void createCoupons(int count, int nThreads) throws InterruptedException {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads); // threadPoolSize 100

        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                couponService.create(couponRequest);
            });
        }
        executorService.shutdown();
        Thread.sleep(1000);
    }

    private void createCoupons(int count) throws InterruptedException {
        createCoupons(count, 100);
    }
}
