package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CouponAnalyzeService {

    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    public CouponAnalyzeService(CurrencyRateHistoryRepository currencyRateHistoryRepository) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
    }

    public void save(RateByBaseCurrency rateByBaseCurrency) {
        final Map<Currency, BigDecimal> currencyRateMap = rateByBaseCurrency.rates;
        List<CurrencyRateHistoryEntity> list = currencyRateMap.keySet().stream()
                .map(currency
                        -> new CurrencyRateHistoryEntity(rateByBaseCurrency.date,
                        currency, currencyRateMap.get(currency)))
                .collect(Collectors.toList());

        currencyRateHistoryRepository.saveAll(list);
    }

    public List<RateByBaseCurrency> getRateByBaseCurrencyByAPI(List<LocalDate> dates) {
        return dates.parallelStream()
                .map(localDate -> {
                    RestTemplate restTemplate = new RestTemplate();
                    String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rateByBaseAndDateUrl = "https://api.vatcomply.com/rates?date=" + date + "&base=USD";
                    //System.out.println("rateByBaseAndDateUrl = " + rateByBaseAndDateUrl);
                    return restTemplate.getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
                }).distinct()
                .collect(Collectors.toList());
    }

    public CouponAnalyzeResult analyzeBy(YearMonth targetDate, Currency currency, boolean isInTest, final CouponService couponService) {

        LocalDate firstDateOfMonth = LocalDate.of(targetDate.getYear(), targetDate.getMonth(), 1);
        LocalDate lastDateOfMonth = LocalDate.of(targetDate.getYear(), targetDate.getMonth(), firstDateOfMonth.lengthOfMonth());
        List<LocalDate> septemberDates = getAllDateInMonthStream(firstDateOfMonth);
        List<Coupon> recentlyCreatedCoupon = null;
        if (isInTest) {
            // 운영환경에서는 API 연동한 환율 데이터와 쿠폰 발급한 데이터가 이미 존재한다.
            List<RateByBaseCurrency> list = getRateByBaseCurrencyByAPI(septemberDates);
            list.forEach(this::save);
            int couponCountPerDay = 100;
            createCoupons(couponService, septemberDates, couponCountPerDay);
            recentlyCreatedCoupon = couponService
                    .findRecentlyCreatedCoupon(firstDateOfMonth.lengthOfMonth() * couponCountPerDay);
        } else {
            //recentlyCreatedCoupon = couponService.findByDate(firstDateOfMonth, lastDateOfMonth);
        }


        List<CurrencyRateHistoryEntity> targetRates = currencyRateHistoryRepository
                .findByDateBetweenAndCurrency(firstDateOfMonth, lastDateOfMonth, currency);

        //Question. 일자별 환율 데이터는 LocalDate를 키로하는 Map 으로 사용하는게 비용이나 속도 측면에서 더 좋아보이는데, 클래스로 선언하는게 맞을지
        List<RateOfDate> rateOfDateList = targetRates.stream()
                .map(currencyRateHistoryEntity -> new RateOfDate(currencyRateHistoryEntity.date, currencyRateHistoryEntity.amount))
                .collect(Collectors.toList());

        List<BigDecimal> convertedCouponAmountList = recentlyCreatedCoupon.parallelStream()
                .map(coupon -> {
                    LocalDate createdDate = coupon.createdTime.toLocalDate();
                    Optional<RateOfDate> maybeRateOfDate = rateOfDateList.stream()
                            .filter(rateOfDate -> rateOfDate.date.equals(createdDate))
                            .findFirst();
                    RateOfDate rateOfDate = maybeRateOfDate.orElseGet(() -> retrieveLastBusinessDay(rateOfDateList, createdDate));
                    BigDecimal rate = rateOfDate.rate;
                    BigDecimal amount = BigDecimal.valueOf(coupon.amountOff);
                    return rate.multiply(amount);
                }).collect(Collectors.toList());

        BigDecimal sumAmountsOfCoupon = convertedCouponAmountList.stream()
                .reduce(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), BigDecimal::add);
        BigDecimal averageAmount = sumAmountsOfCoupon.divide(BigDecimal.valueOf(convertedCouponAmountList.size()));

        return new CouponAnalyzeResult(targetDate.getYear(), targetDate.getMonth(), sumAmountsOfCoupon, averageAmount);
    }

    public RateOfDate retrieveLastBusinessDay(List<RateOfDate> rateOfDateList, LocalDate localDate) {
        LocalDate previousDate = localDate.minus(1, ChronoUnit.DAYS);
        Optional<RateOfDate> mayBeRate = rateOfDateList.stream()
                .filter(rateOfDate -> rateOfDate.date.equals(previousDate))
                .findFirst();
        return mayBeRate
                .orElseGet(() -> retrieveLastBusinessDay(rateOfDateList, previousDate));
    }

    public List<LocalDate> getAllDateInMonthStream(LocalDate firstDateOfMonth) {
        return IntStream.range(0, firstDateOfMonth.lengthOfMonth())
                .mapToObj(firstDateOfMonth::plusDays)
                .collect(Collectors.toList());
    }

    private void createCoupons(CouponService couponService, List<LocalDate> monthDates, int couponCountPerDay) {

        Currency currency = Currency.USD;
        Long couponAmountOff = 100L;
        int numberOfThreads = 4;
        monthDates.forEach(localDate -> {
            // 특정 고정값(USD) 할인 쿠폰
            CouponCreationRequest couponCreationRequest = new CouponCreationRequest(
                    CouponDuration.ONCE, null, DiscountType.AMOUNT, currency, couponAmountOff, null);

            concurrnetCreateCoupons(couponService, couponCreationRequest, localDate, couponCountPerDay, numberOfThreads);
        });
    }

    private void concurrnetCreateCoupons(CouponService couponService, CouponCreationRequest couponCreationRequest, LocalDate localDate, int count, int nThreads) {
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
