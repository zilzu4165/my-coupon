package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    @DisplayName("쿠폰 100개가 동시에 생성됐을 때, 100개가 정상적으로 생성된다.")
    @RepeatedTest(100)
    void test() {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        Runnable runnable = () -> couponService.create(couponRequest);
        for (int i = 0; i < 100; i++) {
            executorService.submit(runnable);
        }
        executorService.shutdown();

        assertThat(couponService.getAllCoupon()).isEqualTo(100);
        // 실패시 밑에있는 로직 건너뜀
    }

    @Test
    @DisplayName("쿠폰 100개가 동시에 생성됐을 때, 100개가 정상적으로 생성된다.")
    @RepeatedTest(100)
    void test1() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            Thread th = new CreateCouponThread();
            th.start();  // 해당 thread 시작
            th.join();   // thread 가 종료될 때 까지 기다린 후에 실행
        }

        assertThat(couponService.getAllCoupon()).isEqualTo(100);

    }

    @Test
    @DisplayName("쿠폰 100개가 동시에 생성됐을 때, 100개가 정상적으로 생성된다.")
    @RepeatedTest(100)
    void test2() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            Thread th = new Thread(() -> {
                CouponRequest couponRequest = new CouponRequest("3", 3);
                couponService.create(couponRequest);
            });
            th.start();
            th.join();
        }

        assertThat(couponService.getAllCoupon()).isEqualTo(100);

    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다.")
    void test4() {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100


        Runnable runnable = () -> {
            for (int i = 0; i < 100; i++) {
                couponService.create(couponRequest);
            }
        };

        for (int i = 0; i < 100; i++) {
            executorService.submit(runnable);
        }
        executorService.shutdown();

        assertThat(couponService.getAllCoupon()).isEqualTo(10000);
    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다.")
    void test5() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            Thread th = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    CouponRequest couponRequest = new CouponRequest("3", 3);
                    couponService.create(couponRequest);
                }
            });
            th.start();
            th.join();
        }
        assertThat(couponService.getAllCoupon()).isEqualTo(10000);
    }

    private class CreateCouponThread extends Thread {
        @Override
        public void run() {
            CouponRequest couponRequest = new CouponRequest("3", 3);
            couponService.create(couponRequest);
        }
    }


}
