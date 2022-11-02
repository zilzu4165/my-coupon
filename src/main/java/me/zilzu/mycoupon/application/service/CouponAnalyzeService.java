package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponAnalyzeService {

    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    private final SameMonthDatesFinder sameMonthDatesFinder;

    private final LastBusinessDayRateFinder lastBusinessDayRateFinder;

    private final RecentlyCreatedCouponFinder recentlyCreatedCouponFinder;

    public CouponAnalyzeService(
            CurrencyRateHistoryRepository currencyRateHistoryRepository,
            SameMonthDatesFinder sameMonthDatesFinder,
            LastBusinessDayRateFinder lastBusinessDayRateFinder,
            RecentlyCreatedCouponFinder recentlyCreatedCouponFinder
    ) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
        this.sameMonthDatesFinder = sameMonthDatesFinder;
        this.lastBusinessDayRateFinder = lastBusinessDayRateFinder;
        this.recentlyCreatedCouponFinder = recentlyCreatedCouponFinder;
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

    public CouponAnalyzeResult analyzeBy(YearMonth targetDate, Currency currency) {

        LocalDate firstDateOfMonth = LocalDate.of(targetDate.getYear(), targetDate.getMonth(), 1);
        LocalDate lastDateOfMonth = LocalDate.of(targetDate.getYear(), targetDate.getMonth(), firstDateOfMonth.lengthOfMonth());

        List<Coupon> recentlyCreatedCoupon = recentlyCreatedCouponFinder.find(targetDate);

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
                    RateOfDate rateOfDate = maybeRateOfDate.orElseGet(() -> lastBusinessDayRateFinder.find(rateOfDateList, createdDate));
                    BigDecimal rate = rateOfDate.rate;
                    BigDecimal amount = BigDecimal.valueOf(coupon.amountOff);
                    return rate.multiply(amount);
                }).collect(Collectors.toList());

        BigDecimal sumAmountsOfCoupon = convertedCouponAmountList.stream()
                .reduce(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), BigDecimal::add);
        BigDecimal averageAmount = sumAmountsOfCoupon.divide(BigDecimal.valueOf(convertedCouponAmountList.size()));

        return new CouponAnalyzeResult(targetDate.getYear(), targetDate.getMonth(), sumAmountsOfCoupon, averageAmount);
    }
}
