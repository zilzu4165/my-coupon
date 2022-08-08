package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
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
    void test() {
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

        // Test worker
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
        assertThat(couponService.getAllCouponSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다. -1")
    void testRunnable() throws ExecutionException, InterruptedException {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        // Test Worker thread
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        for (int i = 0; i < 10000; i++) {
            // Test Worker 쓰레드가 pool-1-thread-1~100 쓰레드에게 작업을 시킨다.
            executorService.submit(() -> {
                // pool-1-thread-1~100
                System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
                couponService.create(couponRequest);
            });
        }

        executorService.shutdown();
        Thread.sleep(5000);
        // Test Worker Thread
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
        assertThat(couponService.getAllCouponSize()).isEqualTo(10000);
    }
}
