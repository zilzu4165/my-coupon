package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import me.zilzu.mycoupon.storage.NewCouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    NewCouponRepository newCouponRepository;

    @Autowired
    CreatedCouponFinder createdCouponFinder;

    @Autowired
    CouponService couponService;

    @Autowired
    RateHistoryCollector rateHistoryCollector;

    @AfterEach
    void clear_repository() {
        currencyRateHistoryRepository.deleteAll();
        newCouponRepository.deleteAll();
    }

    @Test
    @DisplayName("한 달간 하루에 100개의 쿠폰을 발행한다. 저장된 환율 데이터를 기반으로 해당 기간에 발행된 쿠폰들의 KRW 금액 통계를 구한다.")
    void coupons_amountOff_analyze_by_currency() {
        // 테스트 하고자 하는 년월
        YearMonth yearMonth = YearMonth.of(2022, Month.SEPTEMBER);
        LocalDate firstDateOfMonth = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        LocalDate lastDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 30);

        //한달 일자 목록
        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);

        //테스트 쿠폰 데이터 생성
        createCoupons(septemberDates, Currency.USD, 100, 4);

        //테스트 해당기간 환율 데이터 수집 및 저장
        List<RateByBaseCurrency> list = rateHistoryCollector.collect(septemberDates);
        rateHistoryCollector.save(list);

        //검증
        List<Coupon> createdInMonthCoupon = createdCouponFinder.findBy(yearMonth);

        List<CurrencyRateHistoryEntity> septemberKRWRates = currencyRateHistoryRepository.findByDateBetweenAndCurrency(firstDateOfMonth, lastDateOfMonth, Currency.KRW);
        assertThat(septemberKRWRates.size()).isEqualTo(22);

        List<RateOfDate> rateOfDateList = septemberKRWRates.stream()
                .map(currencyRateHistoryEntity -> new RateOfDate(currencyRateHistoryEntity.date, currencyRateHistoryEntity.amount))
                .collect(Collectors.toList());
        assertThat(rateOfDateList.size()).isEqualTo(22L); //주말을 제외한 9월 일수


        List<BigDecimal> couponInKRWAmountList = createdInMonthCoupon.parallelStream()
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

        assertThat(couponInKRWAmountList.size()).isEqualTo(30 * 100);
        BigDecimal sumSeptemberAmount = couponInKRWAmountList.stream()
                .reduce(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), BigDecimal::add);

        BigDecimal averageSeptemberAmount = sumSeptemberAmount.divide(BigDecimal.valueOf(couponInKRWAmountList.size()));

        System.out.println("sumSeptemberAmount = " + sumSeptemberAmount);
        System.out.println("averageSeptemberAmount = " + averageSeptemberAmount);

        //API 에서 받아온 순수 데이터를 기반으로 계산한 평균과 비교한다
        BigDecimal averageKrwRate = calculateByOriginalAPIData(septemberDates);

        assertThat(averageSeptemberAmount.setScale(4, RoundingMode.HALF_UP))
                .isEqualTo(averageKrwRate);

    }

    private BigDecimal calculateByOriginalAPIData(List<LocalDate> septemberDates) {
        List<RateByBaseCurrency> originalList = septemberDates.parallelStream()
                .map(localDate -> {
                    String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rateByBaseAndDateUrl = "https://api.vatcomply.com/rates?date=" + date + "&base=USD";
                    return new RestTemplate().getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
                }).collect(Collectors.toList());
        BigDecimal sumKrwRate = sumKrwAmount(originalList);
        BigDecimal averageKrwRate = sumKrwRate.divide(BigDecimal.valueOf(30L), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100L)) // 쿠폰 할인 가격
                .setScale(4, RoundingMode.HALF_UP);
        return averageKrwRate;
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

    @Test
    @DisplayName("필요한 환율 데이터가 주말일 경우 직전 평일의 데이터를 가져온다.")
    void currencyRate_of_last_business_day() {
        //setting
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);
        List<RateByBaseCurrency> list = rateHistoryCollector.collect(septemberDates);
        List<RateOfDate> rateOfDateList = list.stream()
                .map(rateByBaseCurrency -> new RateOfDate(rateByBaseCurrency.date, rateByBaseCurrency.rates.get(Currency.KRW)))
                .collect(Collectors.toList());

        LocalDate bizDay01 = LocalDate.of(2022, Month.SEPTEMBER, 2);
        LocalDate holiday01 = LocalDate.of(2022, Month.SEPTEMBER, 3);
        LocalDate holiday02 = LocalDate.of(2022, Month.SEPTEMBER, 4);
        LocalDate bizDay02 = LocalDate.of(2022, Month.SEPTEMBER, 5);

        // 검증
        LastBusinessDayRateFinder lastBusinessDayRateFinder = new LastBusinessDayRateFinder();
        RateOfDate holidayRate01 = lastBusinessDayRateFinder.find(rateOfDateList, holiday01);
        RateOfDate holidayRate02 = lastBusinessDayRateFinder.find(rateOfDateList, holiday02);
        RateOfDate bizdayRate01 = rateOfDateList.stream()
                .filter(rateOfDate -> rateOfDate.date.isEqual(bizDay01))
                .findFirst().get();
        RateOfDate bizdayRate02 = rateOfDateList.stream()
                .filter(rateOfDate -> rateOfDate.date.isEqual(bizDay02))
                .findFirst().get();

        // 토/일의 환율 데이터는 금요일의 데이터와 일치하며 월요일의 데이터와 달라야한다
        assertThat(holidayRate01).isNotNull();
        assertThat(holidayRate02).isNotNull();
        assertThat(holidayRate01.rate).isEqualTo(holidayRate02.rate);
        assertThat(holidayRate01.rate).isEqualTo(bizdayRate01.rate);
        assertThat(holidayRate01.rate).isNotEqualTo(bizdayRate02.rate);
    }


    @Test
    @DisplayName("vatcomply API를 통해 수집한 한 달간의 환율 데이터를 DB에 저장한다. ")
    void save_rate_datas() {
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);

        // DB 저장
        List<RateByBaseCurrency> list = rateHistoryCollector.collect(septemberDates);
        rateHistoryCollector.save(list);

        // DB 조회
        LocalDate septemberFirst = LocalDate.of(2022, 9, 1);
        LocalDate septemberLast = LocalDate.of(2022, 9, 30);
        List<CurrencyRateHistoryEntity> septemberList = currencyRateHistoryRepository.findByDateBetween(septemberFirst, septemberLast);
        assertThat(septemberList).isNotNull();
        assertThat(septemberList.size()).isEqualTo(22 * 32); //22days * 32 Currency

        long idCount = septemberList.stream()
                .map(entity -> entity.id)
                .distinct()
                .count();
        long dateCount = septemberList.stream()
                .map(entity -> entity.date)
                .distinct()
                .count();
        //평일만 저장한다
        assertThat(idCount).isEqualTo(22 * 32);
        assertThat(dateCount).isEqualTo(22L);
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
                    CouponDuration.ONCE, null, DiscountType.AMOUNT, currency, 100L, null);

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