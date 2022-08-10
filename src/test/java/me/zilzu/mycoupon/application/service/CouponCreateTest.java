package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Comparator.*;
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
        CouponRequest couponRequest = new CouponRequest("3", 3);
        // Test Worker
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        for (int i = 0; i < 100; i++) {
            // test Worker 쓰레드가 pool-1-thread-1~100 쓰레드에게 작업을 시킨다.
            executorService.submit(() -> {
                // pool-1-thread-1~100
                System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
                couponService.create(couponRequest);
            });
        }
        executorService.shutdown();
        Thread.sleep(2000);
        // Test worker
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
        assertThat(couponService.getAllCouponSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다. -1")
    void testRunnable() throws InterruptedException {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        // Test Worker thread
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        Runnable runnable = () -> {
            // pool-1-thread-1~100
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            couponService.create(couponRequest);
        };

        for (int i = 0; i < 10000; i++) {
            // Test Worker 쓰레드가 pool-1-thread-1~100 쓰레드에게 작업을 시킨다.
            executorService.submit(runnable);
        }

        executorService.shutdown();
        Thread.sleep(5000);
        // Test Worker Thread
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
        assertThat(couponService.getAllCouponSize()).isEqualTo(10000);
    }


    @Test
    @DisplayName("쿠폰을 100개 생성 후, 가장 최근 생성된 10개의 쿠폰 리스트를 내림차순으로 반환한다")
    void test3() throws InterruptedException {

        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        List<Coupon> createdCouponList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                Coupon coupon = couponService.create(couponRequest);
                createdCouponList.add(coupon);
            });
        }
        executorService.shutdown();
        Thread.sleep(1000);


        // 최근 생성된 10개 쿠폰 리스트 내림차순 반환
        List<Coupon> selectRecentlyDescendingCreatedCoupon = couponService.selectRecentlyCreatedCoupon(createdCouponList, 10);

        assertThat(selectRecentlyDescendingCreatedCoupon.size()).isEqualTo(10);

        List<Coupon> sortedCoupon = selectRecentlyDescendingCreatedCoupon.stream()
                .sorted(comparing(Coupon::getDate)          // 오름차순
                        .reversed()                         // 내림차순
                        .thenComparing(Coupon::getCreated)) // 만약에 같으면 이걸로 비교
                .collect(Collectors.toList());

        assertThat(selectRecentlyDescendingCreatedCoupon).isEqualTo(sortedCoupon);


    }

}
