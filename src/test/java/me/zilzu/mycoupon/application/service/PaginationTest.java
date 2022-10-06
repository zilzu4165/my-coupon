package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PaginationTest {

    @Autowired
    CouponService couponService;

    @BeforeEach
    public void emptyCoupon() {
        couponService.emptyCoupon();
    }

    @Test
    @DisplayName("쿠폰 생성시각 기준으로 페이징 조회")
    void test1() throws InterruptedException {
        createCoupons(100, 100);
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("createdTime"));
        Page<Coupon> coupons = couponService.retrieveList(pageRequest);

        List<Coupon> couponList = coupons.getContent();
        Coupon firstCoupon = couponList.get(0);
        Coupon lastCoupon = couponList.get(4);

        assertThat(coupons.getTotalPages()).isEqualTo(20);
        assertThat(coupons.getTotalElements()).isEqualTo(100);
        assertThat(firstCoupon.createdTime).isBefore(lastCoupon.createdTime);
    }

    private void createCoupons(int count, int nThreads) {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, null, null, null);

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads); // threadPoolSize 100

        List<CompletableFuture> futures = IntStream.range(0, count)
                .boxed()
                .map(target -> CompletableFuture.runAsync(() -> couponService.create(couponCreationRequest), executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        executorService.shutdown();
    }
}
