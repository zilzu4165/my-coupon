package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.api.controller.CouponRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

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
    @RepeatedTest(100)
    void test() {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        Runnable runnable = () -> couponService.create(couponRequest);
        for (int i = 0; i < 100; i++) {
            Future<?> future = executorService.submit(runnable);

            try {
                future.get();
                System.out.println("작업완료");
            } catch (Exception e) {
                System.out.println("예외 발생" + e.getMessage());
            }
        }
        executorService.shutdown();

        assertThat(couponService.getAllCouponSize()).isEqualTo(100);
        // 실패시 밑에있는 로직 건너뜀
    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다. -1")
    @RepeatedTest(100)
    void testCallable() throws ExecutionException, InterruptedException {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        Callable<Boolean> callable = () -> {
            Boolean isFinish = true;
            for (int i = 0; i < 100; i++) {
                couponService.create(couponRequest);
            }
            return isFinish;
        };

        for (int i = 0; i < 100; i++) {
            Future<Boolean> isWorkFinish = executorService.submit(callable);

            try {
                Boolean aBoolean = isWorkFinish.get();
                System.out.println(aBoolean);
            } catch (Exception e) {
                System.out.println("실행 예외 발생" + e.getMessage());
            }
        }
        executorService.shutdown();

        assertThat(couponService.getAllCouponSize()).isEqualTo(10000);
    }

    @Test
    @DisplayName("100명의 유저가 동시에 총 10000개의 쿠폰을 생성한다. -2")
    @RepeatedTest(100)
    void testRunnable() throws ExecutionException, InterruptedException {
        CouponRequest couponRequest = new CouponRequest("3", 3);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // threadPoolSize 100

        Runnable runnable = () -> {
            for (int i = 0; i < 100; i++) {
                couponService.create(couponRequest);
            }
        };

        for (int i = 0; i < 100; i++) {
            Future<?> submit = executorService.submit(runnable);

            try {
                submit.get();
                System.out.println("작업완료");
            } catch (Exception e) {
                System.out.println("실행 예외 발생" + e.getMessage());
            }
        }
        executorService.shutdown();

        assertThat(couponService.getAllCouponSize()).isEqualTo(10000);
    }


}
