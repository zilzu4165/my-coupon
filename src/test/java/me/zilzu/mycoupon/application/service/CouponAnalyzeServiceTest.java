package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponAnalyzeServiceTest {
    @Autowired
    CouponAnalyzeService couponAnalyzeService;
    @Autowired
    CurrencyRateHistoryRepository currencyRateHistoryRepository;
    @Autowired
    CouponService couponService;

    private Long couponAmountOff = 100L;

    @Test
    @DisplayName("한 달간 하루에 100개의 쿠폰을 발행한다. 저장된 환율 데이터를 기반으로 해당 기간에 발행된 쿠폰들의 KRW 금액 통계를 구한다.")
    void coupons_amountOff_analyze_by_currency() {
        //한달 일자 목록
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        LocalDate lastDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 30);

        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);

        //쿠폰 생성
        createCoupons(septemberDates, Currency.USD, 100, 4);

        //환율 데이터 수집 및 저장
        List<RateByBaseCurrency> list = couponAnalyzeService.getRateByBaseCurrencyByAPI(septemberDates);
        list.forEach(couponAnalyzeService::save);

        //검증
        List<Coupon> recentlyCreatedCoupon = couponService.findRecentlyCreatedCoupon(30 * 100);

        List<CurrencyRateHistoryEntity> septemberKRWRates = currencyRateHistoryRepository.findByDateBetweenAndCurrency(firstDateOfMonth, lastDateOfMonth, Currency.KRW);
        septemberKRWRates.forEach(System.out::println);

        assertThat(septemberKRWRates.size()).isEqualTo(22);
        System.out.println("septemberKRWRates.size() = " + septemberKRWRates.size());

        List<RateOfDate> rateOfDateList = septemberKRWRates.stream()
                .map(currencyRateHistoryEntity -> new RateOfDate(currencyRateHistoryEntity.date, currencyRateHistoryEntity.amount))
                .collect(Collectors.toList());
//        Map<LocalDate, BigDecimal> krwAmountMap = new HashMap<>();
//        for (CurrencyRateHistoryEntity currencyRateHistoryEntity : septemberKRWRates) {
//            krwAmountMap.put(currencyRateHistoryEntity.date, currencyRateHistoryEntity.amount);
//        }
//        krwAmountMap.keySet().forEach(System.out::println);
//        assertThat(krwAmountMap.keySet().size()).isEqualTo(22L); //주말을 제외한 9월 일수

//        List<BigDecimal> couponInKRWAmountList = recentlyCreatedCoupon.parallelStream()
//                .map(coupon -> {
//                    LocalDate localDate = coupon.createdTime.toLocalDate();
//                    BigDecimal rate = krwAmountMap.get(localDate);
//                    rate = Optional.ofNullable(rate)
//                            .orElseGet(() -> retrieveLastBusinessDay(krwAmountMap, localDate));
//                    BigDecimal amount = BigDecimal.valueOf(coupon.amountOff);
//                    BigDecimal result = rate.multiply(amount);
//                    return result;
//                }).collect(Collectors.toList());


        assertThat(rateOfDateList.size()).isEqualTo(22L); //주말을 제외한 9월 일수
        List<BigDecimal> couponInKRWAmountList = recentlyCreatedCoupon.parallelStream()
                .map(coupon -> {
                    LocalDate createdDate = coupon.createdTime.toLocalDate();
                    Optional<RateOfDate> maybeRateOfDate = rateOfDateList.stream()
                            .filter(rateOfDate -> rateOfDate.date.equals(createdDate))
                            .findFirst();
                    RateOfDate rateOfDate = maybeRateOfDate.orElseGet(() -> new LastBusinessDayRateFinder().find(rateOfDateList, createdDate));
                    BigDecimal rate = rateOfDate.rate;
                    BigDecimal amount = BigDecimal.valueOf(coupon.amountOff);
                    return rate.multiply(amount);
                }).collect(Collectors.toList());

        System.out.println("couponInKRWAmountList.size() = " + couponInKRWAmountList.size());
        assertThat(couponInKRWAmountList.size()).isEqualTo(30 * 100);
        BigDecimal sumSeptemberAmountsOfCoupon = couponInKRWAmountList.stream()
                .reduce(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), BigDecimal::add);

        BigDecimal averageSeptemberAmount = sumSeptemberAmountsOfCoupon.divide(BigDecimal.valueOf(couponInKRWAmountList.size()));

        System.out.println("sumSeptemberAmountsOfCoupon = " + sumSeptemberAmountsOfCoupon);
        System.out.println("averageSeptemberAmount = " + averageSeptemberAmount);

        //API 에서 받아온 순수 데이터를 기반으로 계산한 합/평균과 비교한다
        List<RateByBaseCurrency> originalList = septemberDates.parallelStream()
                .map(localDate -> {
                    String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rateByBaseAndDateUrl = "https://api.vatcomply.com/rates?date=" + date + "&base=USD";
                    return new RestTemplate().getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
                }).collect(Collectors.toList());
        BigDecimal sumKrwRate = sumKrwAmount(originalList);
        System.out.println("sumKrwRate = " + sumKrwRate);
        BigDecimal averageKrwRate = sumKrwRate.divide(BigDecimal.valueOf(30L), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(couponAmountOff)) // 쿠폰 할인 가격
                .setScale(4, RoundingMode.HALF_UP);

        assertThat(averageSeptemberAmount.setScale(4, RoundingMode.HALF_UP))
                .isEqualTo(averageKrwRate);

    }

    private BigDecimal sumKrwAmount(List<RateByBaseCurrency> septemberKRWRates) {
        BigDecimal result = BigDecimal.ZERO;
        List<BigDecimal> krwAmountList = septemberKRWRates.stream()
                .map(rateByBaseCurrency -> rateByBaseCurrency.rates.get(Currency.KRW))
                .collect(Collectors.toList());
        for (BigDecimal bigDecimal : krwAmountList) {
            result = result.add(bigDecimal);
        }
        return result;
    }

    private BigDecimal retrieveLastBusinessDay(Map<LocalDate, BigDecimal> krwAmountMap, LocalDate localDate) {
        LocalDate previousDate = localDate.minus(1, ChronoUnit.DAYS);
        BigDecimal lastRate = krwAmountMap.get(previousDate);
        return Optional.ofNullable(lastRate)
                .orElseGet(() -> retrieveLastBusinessDay(krwAmountMap, previousDate));
    }

    @Test
    @DisplayName("필요한 환율 데이터가 주말일 경우 직전 평일의 데이터를 가져온다.")
    void currencyRate_of_last_business_day() {
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);
        List<RateByBaseCurrency> list = couponAnalyzeService.getRateByBaseCurrencyByAPI(septemberDates);
        assertThat(list.size()).isEqualTo(22);
        list.forEach(System.out::println);

        //setting
        Map<LocalDate, BigDecimal> dateRateMap = new HashMap<>();
        for (RateByBaseCurrency rateByBaseCurrency : list) {
            dateRateMap.put(rateByBaseCurrency.date, rateByBaseCurrency.rates.get(Currency.KRW));
        }


        LocalDate bizDay01 = LocalDate.of(2022, Month.SEPTEMBER, 2);
        LocalDate holiday01 = LocalDate.of(2022, Month.SEPTEMBER, 3);
        LocalDate holiday02 = LocalDate.of(2022, Month.SEPTEMBER, 4);
        LocalDate bizDay02 = LocalDate.of(2022, Month.SEPTEMBER, 5);
        //평일 데이터는 존재하고 주말 데이터는 없어야 한다.
        assertThat(dateRateMap.size()).isEqualTo(22L);
        assertThat(dateRateMap.get(bizDay01)).isNotNull();
        assertThat(dateRateMap.get(holiday01)).isNull();

        BigDecimal holidayRate01 = Optional.ofNullable(dateRateMap.get(holiday01))
                .orElseGet(() -> retrieveLastBusinessDay(dateRateMap, holiday01));

        BigDecimal holidayRate02 = Optional.ofNullable(dateRateMap.get(holiday02))
                .orElseGet(() -> retrieveLastBusinessDay(dateRateMap, holiday02));

        // 토/일의 환율 데이터는 금요일의 데이터와 일치하며 월요일의 데이터와 달라야한다
        assertThat(holidayRate01).isNotNull();
        assertThat(holidayRate02).isNotNull();
        assertThat(holidayRate01).isEqualTo(holidayRate02);
        assertThat(holidayRate01).isEqualTo(dateRateMap.get(bizDay01));
        assertThat(holidayRate01).isNotEqualTo(dateRateMap.get(bizDay02));
    }


    @Test
    @DisplayName("vatcomply API를 통해 수집한 한 달간의 환율 데이터를 DB에 저장한다. ")
    void save_rate_datas() {
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);
        assertThat(septemberDates.size()).isEqualTo(30);

        List<RateByBaseCurrency> list = couponAnalyzeService.getRateByBaseCurrencyByAPI(septemberDates);

        assertThat(list.size()).isEqualTo(22);
        list.forEach(couponAnalyzeService::save);

        LocalDate septemberFirst = LocalDate.of(2022, 9, 1);
        LocalDate septemberLast = LocalDate.of(2022, 9, 30);
        List<CurrencyRateHistoryEntity> septemberList = currencyRateHistoryRepository.findByDateBetween(septemberFirst, septemberLast);
        assertThat(septemberList).isNotNull();
        assertThat(septemberList.size()).isEqualTo(22 * 32); //22days * 32 Currency
        System.out.println("septemberList.size() = " + septemberList.size());

        long idCount = septemberList.stream()
                .map(entity -> entity.id)
                .distinct()
                .count();
        long dateCount = septemberList.stream()
                .map(entity -> entity.date)
                .distinct()
                .count();
        assertThat(idCount).isEqualTo(22 * 32);
        //평일만 저장한다
        assertThat(dateCount).isEqualTo(22L);

        List<CurrencyRateHistoryEntity> byDateAndCurrency = currencyRateHistoryRepository.findByDateAndCurrency(firstDateOfMonth, Currency.KRW);
        System.out.println("byDateAndCurrency = " + byDateAndCurrency);
    }

    @Test
    @DisplayName("2022.09 한 달간 매일 100장의 쿠폰을 발행한다.")
    void create_and_apply_coupons_everyday_in_a_month() {
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);

        createCoupons(septemberDates, Currency.USD, 100, 4);

        Long allCouponSize = couponService.getAllCouponSize();
        assertThat(allCouponSize).isEqualTo(30 * 100); // days * count
    }

    public void createCoupons(List<LocalDate> monthDates, Currency currency, int count, int nThreads) {

        monthDates.forEach(localDate -> {
            //xx.xx% 할인 쿠폰 / USD
//            Double discountPercent = Math.round(Math.random() * 10000) / 100.0;
//            System.out.println("discountPercent = " + discountPercent);
//            new CouponCreationRequest(CouponDuration.ONCE, null, DiscountType.PERCENTAGE, currency, null, discountPercent);

            // 100 USD 고정할인 쿠폰
            CouponCreationRequest couponCreationRequest = new CouponCreationRequest(
                    CouponDuration.ONCE, null, DiscountType.AMOUNT, currency, couponAmountOff, null);

            concurrnetCreateCoupons(couponCreationRequest, localDate, count, nThreads);
        });
    }

    private void concurrnetCreateCoupons(CouponCreationRequest couponCreationRequest, LocalDate localDate, int count, int nThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads); // threadPoolSize 100

        List<CompletableFuture> futures = IntStream.range(0, count)
                .boxed()
                .map(target ->
                        CompletableFuture.runAsync(() ->
                                couponService.create(couponCreationRequest, localDate.atStartOfDay()), executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        executorService.shutdown();
    }

}