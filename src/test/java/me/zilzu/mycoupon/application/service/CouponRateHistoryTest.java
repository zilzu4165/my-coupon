package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
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
    @Autowired
    private Summarizer summarizer;


    @BeforeEach
    void emptyCoupon() {
        couponService.emptyCoupon();
    }

    @Test
    @DisplayName("1.해당 월에 생성된 쿠폰을 전부 조회한다. 2. 해당 월의 환율을 전부 가져온다. 3.환율 계산해서 KRW로 보여준다.")
    void test1() throws InterruptedException {
        createRandomCouponsInMonth(100, 100);

        String yearMonthStr = "2022-09";
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyy-MM"));

        // 1. 해당월에 생성된 쿠폰을 전부 조회한다.
        List<Coupon> foundCouponsOfMonth = couponService.retrieveAllCouponOfMonth(yearMonth, Currency.USD);

        assertThat(foundCouponsOfMonth.size()).isEqualTo(100);

        // 2. 해당 월의 환율을 가져온다.
        List<CouponRateHistory> couponRateHistories = couponRateExchanger.getRateOfMonth(yearMonth);

        // 3. 해당 통화로 환율을 계산한다.
        List<CouponRateCalculationResult> couponRateCalculationResults = couponRateExchanger.calculateRateExchanger(foundCouponsOfMonth, couponRateHistories, Currency.KRW);

        assertThat(couponRateCalculationResults.size()).isEqualTo(foundCouponsOfMonth.size());

        // 3-1 2022-09-01에 [생성된 쿠폰, 환율이 계산된 result, 환율] 로 계산이 잘 되었는지 검증하기
//        verifyCalculation(foundCouponsOfMonth,couponRateHistories,couponRateCalculationResults);

        // 4. 통계를 낸다. 합계, 평균
        CouponStatsResult statsResult = summarizer.summarize(couponRateCalculationResults, Currency.KRW);
        BigDecimal sum = couponRateCalculationResults.stream()
                .map(result -> result.calculatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(BigDecimal.valueOf(couponRateCalculationResults.size()));

        System.out.println("sum = " + sum);
        System.out.println("average = " + average);

        assertThat(statsResult.currency).isEqualTo(Currency.KRW);
        assertThat(statsResult.sum).isEqualTo(sum);
        assertThat(statsResult.average).isEqualTo(average);


    }

    public void createRandomCouponsInMonth(int count, int nThreads) throws InterruptedException {
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
        createRandomCouponsInMonth(count, 100);
    }
}
