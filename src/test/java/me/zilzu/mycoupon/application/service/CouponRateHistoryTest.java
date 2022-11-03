package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CouponRateHistoryTest {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponIdGenerator couponIdGenerator;
    @Autowired
    private CouponRateExchanger couponRateExchanger;


    @BeforeEach
    void emptyCoupon() {
        couponService.emptyCoupon();
    }

    @Test
    @DisplayName("1.해당 월에 생성된 쿠폰을 전부 조회한다. 2. 해당 월의 환율을 전부 가져온다. 3.환율 계산해서 KRW로 보여준다.")
    void test1() throws InterruptedException {
        int count = 100;
        createCoupons(count, 100);

        String yearMonthStr = "2022-09";
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyy-MM"));

        // 1. 해당월에 생성된 쿠폰을 전부 조회한다.
        List<Coupon> foundCouponsOfMonth = couponService.retrieveAllCouponOfMonth(yearMonth, Currency.USD);

        foundCouponsOfMonth.forEach(System.out::println);
        assertThat(foundCouponsOfMonth.size()).isEqualTo(count);

        System.out.println("couponSize = " + foundCouponsOfMonth.size());

        // 2. 해당 월의 환율을 가져온다.
        List<CouponRateHistory> couponRateHistories = couponRateExchanger.getRateOfMonth(yearMonth);

        // 3. KRW로 환율을 계산한다.
        List<CouponRateCalculationResult> couponRateCalculationResults = couponRateExchanger.calculateRateExchanger(foundCouponsOfMonth, couponRateHistories);

        System.out.println("couponRateCalculationResults.size() = " + couponRateCalculationResults.size());

        assertThat(couponRateCalculationResults.size()).isEqualTo(foundCouponsOfMonth.size());

    }

    public void createCoupons(int count, int nThreads) throws InterruptedException {
        CouponCreationRequest couponCreationRequest = new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.AMOUNT, Currency.USD, 1L, null);

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads); // threadPoolSize 100

        List<CompletableFuture> futures = IntStream.range(0, count)
                .boxed()
                .map(target ->
                        CompletableFuture.runAsync(() ->
                        {
                            String yearMonthStr = "2022-09";
                            YearMonth yearMonth = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
                            int atDay = yearMonth.lengthOfMonth();
                            Integer randomDay = (int) (Math.random() * atDay) + 1;
                            String day = "";
                            if (randomDay < 10) {
                                day = "0" + randomDay;
                            } else {
                                day = String.valueOf(randomDay);
                            }
                            couponService.create(couponCreationRequest, LocalDate.parse(yearMonth + "-" + day, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
                        }, executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        executorService.shutdown();
    }

    private void createCoupons(int count) throws InterruptedException {
        createCoupons(count, 100);
    }
}
